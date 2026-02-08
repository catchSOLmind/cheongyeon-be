package com.catchsolmind.cheongyeonbe.domain.task.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.*;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.*;
import com.catchsolmind.cheongyeonbe.domain.task.entity.*;
import com.catchsolmind.cheongyeonbe.domain.task.repository.*;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MyTaskCommandService {

    private final TaskTypeRepository taskTypeRepository;
    private final TaskRepository taskRepository;
    private final TaskOccurrenceRepository occurrenceRepository;
    private final TaskLogRepository taskLogRepository;
    private final TaskTakeoverRepository taskTakeoverRepository;
    private final TaskPostponeLogRepository taskPostponeLogRepository;
    private final TaskIncompleteLogRepository taskIncompleteLogRepository;
    private final GroupMemberRepository groupMemberRepository;

    public MyTaskCreateResponse createMyTasks(GroupMember member, MyTaskCreateRequest req) {
        Group group = member.getGroup();

        List<TaskType> types = taskTypeRepository.findAllById(req.getTaskTypeIds());
        if (types.size() != req.getTaskTypeIds().size()) {
            throw new IllegalArgumentException("Some taskTypeIds are invalid");
        }

        // 담당자 결정: assigneeMemberId가 있으면 해당 멤버, 없으면 본인
        GroupMember assignee = member;
        if (req.getAssigneeMemberId() != null) {
            assignee = groupMemberRepository.findById(req.getAssigneeMemberId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
        }

        // 반복 규칙 변환
        String repeatRule = null;
        if (req.getRepeat() != null && Boolean.TRUE.equals(req.getRepeat().getEnabled())) {
            repeatRule = convertDaysToRRule(req.getRepeat().getDaysOfWeek());
        }

        List<MyTaskCreateResponse.CreatedMyTaskDto> created = new ArrayList<>();

        for (TaskType type : types) {
            Task task = Task.builder()
                    .group(group)
                    .taskType(type)
                    .title(type.getName())
                    .creatorMember(member)
                    .repeatRule(repeatRule)
                    .time(req.getTime())
                    .status(TaskStatus.WAITING)
                    .build();
            taskRepository.save(task);

            TaskOccurrence occ = TaskOccurrence.builder()
                    .task(task)
                    .group(group)
                    .occurDate(req.getDate())
                    .primaryAssignedMember(assignee)
                    .status(TaskStatus.WAITING)
                    .build();
            occurrenceRepository.save(occ);

            created.add(MyTaskCreateResponse.CreatedMyTaskDto.builder()
                    .taskId(task.getTaskId())
                    .occurrenceId(occ.getOccurrenceId())
                    .taskTypeId(type.getTaskTypeId())
                    .taskName(type.getName())
                    .point(type.getPoint())
                    .time(task.getTime())
                    .assignee(MyTaskCreateResponse.AssigneeDto.builder()
                            .memberId(assignee.getGroupMemberId())
                            .nickname(assignee.getUser().getNickname())
                            .profileImageUrl(assignee.getUser().getProfileImg())
                            .build())
                    .repeat(MyTaskCreateResponse.RepeatDto.builder()
                            .enabled(repeatRule != null && !repeatRule.isBlank())
                            .daysOfWeek(parseRRuleToDaysOfWeek(repeatRule))
                            .build())
                    .build());
        }

        return MyTaskCreateResponse.builder()
                .createdCount(created.size())
                .created(created)
                .build();
    }

    // 내 할 일 상태 변경하기
    public MyTaskStatusUpdateResponse updateStatus(
            GroupMember member,
            Long occurrenceId,
            MyTaskStatusUpdateRequest req
    ) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndPrimaryAssignedMember_GroupMemberId(occurrenceId, member.getGroupMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        LocalDateTime now = LocalDateTime.now();
        TaskStatus target = req.getStatus();

        if (target == TaskStatus.COMPLETED) {
            occ.setStatus(TaskStatus.COMPLETED);

            if (!taskLogRepository.existsByOccurrence_OccurrenceId(occurrenceId)) {
                taskLogRepository.save(
                        TaskLog.builder()
                                .occurrence(occ)
                                .doneByMember(member)
                                .memo(null)
                                .build()
                );
            }

        } else {
            occ.setStatus(target);
            taskLogRepository.deleteByOccurrence_OccurrenceId(occurrenceId);
        }

        // INCOMPLETED 사유 처리 및 로그 저장
        MyTaskStatusUpdateResponse.IncompleteReasonDto incompleteReason = null;
        if (target == TaskStatus.INCOMPLETED && req.getReasonCode() != null) {
            incompleteReason = MyTaskStatusUpdateResponse.IncompleteReasonDto.builder()
                    .reasonCode(req.getReasonCode())
                    .reasonText(req.getReasonText())
                    .build();

            // 미완료 로그 저장 (리포트용)
            taskIncompleteLogRepository.save(
                    TaskIncompleteLog.builder()
                            .occurrence(occ)
                            .member(member)
                            .reasonCode(req.getReasonCode())
                            .build()
            );
        }

        return MyTaskStatusUpdateResponse.builder()
                .occurrenceId(occurrenceId)
                .status(occ.getStatus())
                .incompleteReason(incompleteReason)
                .updatedAt(now)
                .build();
    }

    // 내 할 일 일정 변경하기
    public MyTaskScheduleUpdateResponse updateSchedule(
            GroupMember member,
            Long occurrenceId,
            MyTaskScheduleUpdateRequest req
    ) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndPrimaryAssignedMember_GroupMemberId(occurrenceId, member.getGroupMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        Task task = occ.getTask();
        LocalDate originalDate = occ.getOccurDate();

        // 일시 변경
        if (req.getDate() != null) {
            occ.setOccurDate(LocalDate.parse(req.getDate()));
        }

        // 시간 변경
        if (req.getTime() != null) {
            task.setTime(req.getTime());
        }

        taskRepository.save(task);
        occurrenceRepository.save(occ);

        // 미루는 사유 처리 및 로그 저장
        MyTaskScheduleUpdateResponse.PostponeReasonDto postponeReason = null;
        if (req.getPostponeReasonCode() != null) {
            postponeReason = MyTaskScheduleUpdateResponse.PostponeReasonDto.builder()
                    .reasonCode(req.getPostponeReasonCode())
                    .reasonText(req.getPostponeReasonText())
                    .build();

            // 일정 미룸 로그 저장 (리포트용)
            taskPostponeLogRepository.save(
                    TaskPostponeLog.builder()
                            .occurrence(occ)
                            .member(member)
                            .originalDate(originalDate)
                            .newDate(occ.getOccurDate())
                            .reasonCode(req.getPostponeReasonCode())
                            .build()
            );
        }

        return MyTaskScheduleUpdateResponse.builder()
                .occurrenceId(occurrenceId)
                .date(occ.getOccurDate().toString())
                .time(task.getTime())
                .postponeReason(postponeReason)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 내 할 일 멤버에게 부탁하기
    public MyTaskRequestToMemberResponse requestToMember(
            GroupMember member,
            Long occurrenceId,
            MyTaskRequestToMemberRequest req
    ) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndPrimaryAssignedMember_GroupMemberId(occurrenceId, member.getGroupMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        GroupMember toMember = groupMemberRepository.findById(req.getToMemberId())
                .orElseThrow(() -> new IllegalArgumentException("To member not found"));

        // 담당자 변경
        occ.setPrimaryAssignedMember(toMember);
        occurrenceRepository.save(occ);

        // 부탁하기 로그 저장 (리포트용)
        taskTakeoverRepository.save(
                TaskTakeover.builder()
                        .occurrence(occ)
                        .fromMember(member)
                        .toMember(toMember)
                        .build()
        );

        return MyTaskRequestToMemberResponse.builder()
                .occurrenceId(occurrenceId)
                .fromMemberId(member.getGroupMemberId())
                .toMemberId(req.getToMemberId())
                .updatedAssigneeMemberId(toMember.getGroupMemberId())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 내 할 일 수정
    public MyTaskUpdateResponse updateMyTask(
            GroupMember member,
            Long occurrenceId,
            MyTaskUpdateRequest request
    ) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndPrimaryAssignedMember_GroupMemberId(occurrenceId, member.getGroupMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        Task task = occ.getTask();

        // 1. taskTypeId 변경
        if (request.getTaskTypeId() != null) {
            TaskType newType = taskTypeRepository.findById(request.getTaskTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("TaskType not found"));
            task.setTaskType(newType);
            task.setTitle(newType.getName());
        }

        // 2. date 변경
        if (request.getDate() != null) {
            occ.setOccurDate(LocalDate.parse(request.getDate()));
        }

        // 3. time 변경
        if (request.getTime() != null) {
            task.setTime(request.getTime());
        }

        // 4. repeat 변경
        if (request.getRepeat() != null) {
            if (Boolean.TRUE.equals(request.getRepeat().getEnabled())) {
                // 요일 목록 → RRULE 변환
                String rrule = convertDaysToRRule(request.getRepeat().getDaysOfWeek());
                task.setRepeatRule(rrule);
            } else {
                task.setRepeatRule(null);
            }
        }

        // 5. assigneeMemberId 변경
        if (request.getAssigneeMemberId() != null) {
            GroupMember newAssignee = groupMemberRepository.findById(request.getAssigneeMemberId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            occ.setPrimaryAssignedMember(newAssignee);
        }

        taskRepository.save(task);
        occurrenceRepository.save(occ);

        // 응답 구성
        GroupMember assignee = occ.getPrimaryAssignedMember();
        String repeatRule = task.getRepeatRule();

        return MyTaskUpdateResponse.builder()
                .occurrenceId(occ.getOccurrenceId())
                .taskId(task.getTaskId())
                .taskType(MyTaskUpdateResponse.TaskTypeDto.builder()
                        .taskTypeId(task.getTaskType().getTaskTypeId())
                        .category(task.getTaskType().getCategory())
                        .name(task.getTaskType().getName())
                        .point(task.getTaskType().getPoint())
                        .build())
                .date(occ.getOccurDate().toString())
                .time(task.getTime())
                .repeat(MyTaskUpdateResponse.RepeatDto.builder()
                        .enabled(repeatRule != null && !repeatRule.isBlank())
                        .daysOfWeek(parseRRuleToDaysOfWeek(repeatRule))
                        .build())
                .assignee(MyTaskUpdateResponse.AssigneeDto.builder()
                        .memberId(assignee.getGroupMemberId())
                        .nickname(assignee.getUser().getNickname())
                        .profileImageUrl(assignee.getUser().getProfileImg())
                        .build())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 내 할 일 완료하기 (간편 API)
    public MyTaskCompleteResponse completeTask(GroupMember member, Long occurrenceId) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndPrimaryAssignedMember_GroupMemberId(occurrenceId, member.getGroupMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        // 이미 완료된 경우 체크
        if (occ.getStatus() == TaskStatus.COMPLETED) {
            throw new IllegalArgumentException("이미 완료된 할일입니다.");
        }

        LocalDateTime now = LocalDateTime.now();

        // 상태 완료로 변경
        occ.setStatus(TaskStatus.COMPLETED);
        occurrenceRepository.save(occ);

        // TaskLog 저장 (완료 기록)
        if (!taskLogRepository.existsByOccurrence_OccurrenceId(occurrenceId)) {
            taskLogRepository.save(
                    TaskLog.builder()
                            .occurrence(occ)
                            .doneByMember(member)
                            .memo(null)
                            .build()
            );
        }

        // 포인트 조회
        Integer earnedPoint = occ.getTask().getTaskType().getPoint();

        return MyTaskCompleteResponse.builder()
                .occurrenceId(occurrenceId)
                .status(TaskStatus.COMPLETED)
                .earnedPoint(earnedPoint)
                .completedAt(now.toString())
                .build();
    }

    // 내 할 일 삭제하기
    public MyTaskDeleteResponse deleteMyTask(
            GroupMember member,
            Long occurrenceId
    ) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndPrimaryAssignedMember_GroupMemberId(occurrenceId, member.getGroupMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        // 관련 로그 삭제
        taskLogRepository.deleteByOccurrence_OccurrenceId(occurrenceId);

        // occurrence 삭제
        occurrenceRepository.delete(occ);

        return MyTaskDeleteResponse.builder()
                .occurrenceId(occurrenceId)
                .deletedAt(LocalDateTime.now())
                .build();
    }

    private String convertDaysToRRule(List<String> daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            return null;
        }

        String byDay = daysOfWeek.stream()
                .map(this::convertToShortDayName)
                .collect(Collectors.joining(","));

        return "FREQ=WEEKLY;BYDAY=" + byDay;
    }

    private String convertToShortDayName(String fullDay) {
        switch (fullDay.toUpperCase()) {
            case "MON": return "MO";
            case "TUE": return "TU";
            case "WED": return "WE";
            case "THU": return "TH";
            case "FRI": return "FR";
            case "SAT": return "SA";
            case "SUN": return "SU";
            default: return fullDay.substring(0, 2);
        }
    }

    private List<String> parseRRuleToDaysOfWeek(String rrule) {
        if (rrule == null || rrule.isBlank()) {
            return List.of();
        }

        String[] parts = rrule.split(";");
        for (String part : parts) {
            if (part.startsWith("BYDAY=")) {
                String daysStr = part.substring("BYDAY=".length());
                return Arrays.stream(daysStr.split(","))
                        .map(this::convertToFullDayName)
                        .collect(Collectors.toList());
            }
        }

        return List.of();
    }

    private String convertToFullDayName(String shortDay) {
        switch (shortDay.toUpperCase()) {
            case "MO": return "MON";
            case "TU": return "TUE";
            case "WE": return "WED";
            case "TH": return "THU";
            case "FR": return "FRI";
            case "SA": return "SAT";
            case "SU": return "SUN";
            default: return shortDay;
        }
    }
}