package com.catchsolmind.cheongyeonbe.domain.task.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.MyTaskCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.MyTaskStatusUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.MyTaskUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskStatusUpdateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskUpdateResponse;
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
    private final GroupMemberRepository groupMemberRepository;

    public MyTaskCreateResponse createMyTasks(Long myMemberId, MyTaskCreateRequest req) {

        GroupMember me = groupMemberRepository.findById(myMemberId)
                .orElseThrow(() -> new IllegalArgumentException("GroupMember not found"));

        Group group = me.getGroup();

        List<TaskType> types = taskTypeRepository.findAllById(req.getTaskTypeIds());
        if (types.size() != req.getTaskTypeIds().size()) {
            throw new IllegalArgumentException("Some taskTypeIds are invalid");
        }

        List<MyTaskCreateResponse.CreatedMyTaskDto> created = new ArrayList<>();

        for (TaskType type : types) {
            Task task = Task.builder()
                    .group(group)
                    .taskType(type)
                    .title(type.getName())
                    .creatorMember(me)
                    .repeatRule(null)
                    .time(null)
                    .status(TaskStatus.UNCOMPLETED)
                    .build();
            taskRepository.save(task);

            TaskOccurrence occ = TaskOccurrence.builder()
                    .task(task)
                    .group(group)
                    .occurDate(req.getDate())
                    .primaryAssignedMember(me)
                    .status(TaskStatus.UNCOMPLETED)
                    .build();
            occurrenceRepository.save(occ);

            created.add(MyTaskCreateResponse.CreatedMyTaskDto.builder()
                    .taskId(task.getTaskId())
                    .occurrenceId(occ.getOccurrenceId())
                    .taskTypeId(type.getTaskTypeId())
                    .taskName(type.getName())
                    .point(type.getPoint())
                    .build());
        }

        return MyTaskCreateResponse.builder()
                .createdCount(created.size())
                .created(created)
                .build();
    }

    // 내 할 일 수정
    public MyTaskUpdateResponse updateMyTask(
            Long groupId,
            Long occurrenceId,
            MyTaskUpdateRequest request
    ) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndGroup_GroupId(occurrenceId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        Task task = occ.getTask();

        // ✅ 1. taskTypeId 변경
        if (request.getTaskTypeId() != null) {
            TaskType newType = taskTypeRepository.findById(request.getTaskTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("TaskType not found"));
            task.setTaskType(newType);
            task.setTitle(newType.getName());
        }

        // ✅ 2. date 변경
        if (request.getDate() != null) {
            occ.setOccurDate(LocalDate.parse(request.getDate()));
        }

        // ✅ 3. time 변경
        if (request.getTime() != null) {
            task.setTime(request.getTime());
        }

        // ✅ 4. repeat 변경
        if (request.getRepeat() != null) {
            if (Boolean.TRUE.equals(request.getRepeat().getEnabled())) {
                // 요일 목록 → RRULE 변환
                String rrule = convertDaysToRRule(request.getRepeat().getDaysOfWeek());
                task.setRepeatRule(rrule);
            } else {
                task.setRepeatRule(null);
            }
        }

        // ✅ 5. assigneeMemberId 변경
        if (request.getAssigneeMemberId() != null) {
            GroupMember newAssignee = groupMemberRepository.findById(request.getAssigneeMemberId())
                    .orElseThrow(() -> new IllegalArgumentException("Assignee not found"));
            occ.setPrimaryAssignedMember(newAssignee);
        }

        taskRepository.save(task);
        occurrenceRepository.save(occ);

        // ✅ 응답 구성
        GroupMember assignee = occ.getPrimaryAssignedMember();
        String repeatRule = task.getRepeatRule();

        return MyTaskUpdateResponse.builder()
                .occurrenceId(occ.getOccurrenceId())
                .taskId(task.getTaskId())
                .taskType(MyTaskUpdateResponse.TaskTypeDto.builder()
                        .taskTypeId(task.getTaskType().getTaskTypeId())
                        .category(task.getTaskType().getCategory())
                        .name(task.getTaskType().getName())
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

    public MyTaskStatusUpdateResponse updateStatus(
            Long myMemberId,
            Long groupId,
            Long occurrenceId,
            MyTaskStatusUpdateRequest req
    ) {
        TaskOccurrence occ = occurrenceRepository
                .findByOccurrenceIdAndGroup_GroupId(occurrenceId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        GroupMember me = groupMemberRepository.findById(myMemberId)
                .orElseThrow(() -> new IllegalArgumentException("GroupMember not found"));

        LocalDateTime now = LocalDateTime.now();
        TaskStatus target = req.getStatus();

        Long doneByMemberId = null;
        LocalDateTime doneAt = null;

        if (target == TaskStatus.COMPLETED) {
            occ.setStatus(TaskStatus.COMPLETED);

            if (!taskLogRepository.existsByOccurrence_OccurrenceId(occurrenceId)) {
                taskLogRepository.save(
                        TaskLog.builder()
                                .occurrence(occ)
                                .doneByMember(me)
                                .memo(null)
                                .build()
                );
            }

            doneByMemberId = myMemberId;
            doneAt = now;

        } else {
            occ.setStatus(TaskStatus.UNCOMPLETED);
            taskLogRepository.deleteByOccurrence_OccurrenceId(occurrenceId);
        }

        return MyTaskStatusUpdateResponse.builder()
                .occurrenceId(occurrenceId)
                .status(occ.getStatus())
                .doneByMemberId(doneByMemberId)
                .doneAt(doneAt)
                .updatedAt(now)
                .build();
    }

    /**
     * ["MON", "WED", "FRI"] → "FREQ=WEEKLY;BYDAY=MO,WE,FR"
     */
    private String convertDaysToRRule(List<String> daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            return null;
        }

        String byDay = daysOfWeek.stream()
                .map(this::convertToShortDayName)
                .collect(Collectors.joining(","));

        return "FREQ=WEEKLY;BYDAY=" + byDay;
    }

    /**
     * MON → MO, TUE → TU 등으로 변환
     */
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