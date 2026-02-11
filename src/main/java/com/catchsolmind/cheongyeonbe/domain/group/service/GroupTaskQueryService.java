package com.catchsolmind.cheongyeonbe.domain.group.service;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.Agreement;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementRepository;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.ManagerCallResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.service.EraserService;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskCalendarResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskDetailResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskListResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.Task;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskTakeoverRepository;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupTaskQueryService {

    private final TaskOccurrenceRepository occurrenceRepository;
    private final TaskTakeoverRepository takeoverRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final AgreementRepository agreementRepository;
    private final EraserService eraserService;

    public GroupTaskListResponse getGroupTasks(Long groupId, LocalDate selectedDate) {
        long activeMemberCount = groupMemberRepository.countByGroup_GroupIdAndStatusNot(groupId, MemberStatus.LEFT);
        boolean isSoloGroup = activeMemberCount < 2;


        Agreement agreement = agreementRepository.findByGroup_GroupIdAndDeletedAtIsNull(groupId)
                .orElse(null);
        String agreementStatus;
        if (agreement == null) {
            agreementStatus = "NONE";
        } else {
            agreementStatus = agreement.getStatus().name();
        }

        boolean isAllMembersAgreed = false;
        if (agreement != null) {
            agreementStatus = agreement.getStatus().name();

            int signedCount = agreement.getSigns().size();
            if (signedCount >= activeMemberCount && activeMemberCount > 0) {
                isAllMembersAgreed = true;
            }
        }

        LocalDate weekStart = selectedDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<LocalDate> weekDates = weekStart.datesUntil(weekEnd.plusDays(1))
                .collect(Collectors.toList());

        List<TaskOccurrence> occurrences =
                occurrenceRepository.findByGroup_GroupIdAndOccurDate(groupId, selectedDate);


        List<GroupTaskListResponse.GroupTaskItemDto> items = occurrences.stream()
                .map(occ -> {
                    boolean isTakeover = takeoverRepository.existsByOccurrence_OccurrenceId(occ.getOccurrenceId());
                    GroupMember assignee = occ.getPrimaryAssignedMember();

                    return GroupTaskListResponse.GroupTaskItemDto.builder()
                            .occurrenceId(occ.getOccurrenceId())
                            .taskId(occ.getTask().getTaskId())
                            .taskTypeId(occ.getTask().getTaskType().getTaskTypeId())
                            .taskName(occ.getTask().getTaskType().getName())
                            .category(occ.getTask().getTaskType().getCategory())
                            .point(occ.getTask().getTaskType().getPoint())
                            .time(occ.getTask().getTime())
                            .status(occ.getStatus())
                            .isTakeover(isTakeover)
                            .assignee(GroupTaskListResponse.AssigneeDto.builder()
                                    .memberId(assignee.getGroupMemberId())
                                    .nickname(assignee.getUser().getNickname())
                                    .profileImageUrl(assignee.getUser().getProfileImg())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());

        // 멤버별 요약: occurrences를 assignee 기준으로 그룹핑
        Map<Long, List<TaskOccurrence>> byMember = occurrences.stream()
                .collect(Collectors.groupingBy(occ -> occ.getPrimaryAssignedMember().getGroupMemberId()));

        List<GroupTaskListResponse.MemberTaskSummaryDto> memberSummaries = byMember.entrySet().stream()
                .map(entry -> {
                    GroupMember member = entry.getValue().get(0).getPrimaryAssignedMember();
                    return GroupTaskListResponse.MemberTaskSummaryDto.builder()
                            .memberId(member.getGroupMemberId())
                            .nickname(member.getUser().getNickname())
                            .profileImageUrl(member.getUser().getProfileImg())
                            .taskCount(entry.getValue().size())
                            .build();
                })
                .collect(Collectors.toList());

        List<ManagerCallResponse> managerCalls = eraserService.getManagerCalls(groupId, selectedDate);

        return GroupTaskListResponse.builder()
                .soloGroup(isSoloGroup)
                .allMembersAgreed(isAllMembersAgreed)
                .agreementStatus(agreementStatus)
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .weekDates(weekDates)
                .selectedDate(selectedDate)
                .totalTaskCount(occurrences.size())
                .assignedMemberCount(byMember.size())
                .memberSummaries(memberSummaries)
                .items(items)
                .managerCall(managerCalls)
                .build();
    }

    public GroupTaskCalendarResponse getGroupTaskCalendar(Long groupId, YearMonth yearMonth) {
        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        List<LocalDate> taskDates = occurrenceRepository
                .findDistinctOccurDatesByGroupIdAndDateBetween(groupId, start, end);

        return GroupTaskCalendarResponse.builder()
                .year(yearMonth.getYear())
                .month(yearMonth.getMonthValue())
                .taskDates(taskDates)
                .build();
    }

    public GroupTaskDetailResponse getGroupTaskDetail(Long groupId, Long occurrenceId) {
        TaskOccurrence occ = occurrenceRepository.findByOccurrenceIdAndGroup_GroupId(occurrenceId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        Task task = occ.getTask();
        String repeatRule = task.getRepeatRule();
        boolean repeatEnabled = repeatRule != null && !repeatRule.isBlank();
        List<String> daysOfWeek = parseRRuleToDaysOfWeek(repeatRule);

        GroupMember assignee = occ.getPrimaryAssignedMember();
        boolean isTakeover = takeoverRepository.existsByOccurrence_OccurrenceId(occ.getOccurrenceId());

        return GroupTaskDetailResponse.builder()
                .occurrenceId(occ.getOccurrenceId())
                .taskId(task.getTaskId())
                .groupId(groupId)
                .taskType(GroupTaskDetailResponse.TaskTypeDto.builder()
                        .taskTypeId(task.getTaskType().getTaskTypeId())
                        .category(task.getTaskType().getCategory())
                        .name(task.getTaskType().getName())
                        .point(task.getTaskType().getPoint())
                        .build())
                .date(occ.getOccurDate().toString())
                .time(task.getTime())
                .repeat(GroupTaskDetailResponse.RepeatDto.builder()
                        .enabled(repeatEnabled)
                        .daysOfWeek(daysOfWeek)
                        .build())
                .assignee(GroupTaskDetailResponse.AssigneeDto.builder()
                        .memberId(assignee.getGroupMemberId())
                        .nickname(assignee.getUser().getNickname())
                        .profileImageUrl(assignee.getUser().getProfileImg())
                        .build())
                .status(occ.getStatus())
                .isTakeover(isTakeover)
                .build();
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
            case "MO":
                return "MON";
            case "TU":
                return "TUE";
            case "WE":
                return "WED";
            case "TH":
                return "THU";
            case "FR":
                return "FRI";
            case "SA":
                return "SAT";
            case "SU":
                return "SUN";
            default:
                return shortDay;
        }
    }
}
