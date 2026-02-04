package com.catchsolmind.cheongyeonbe.domain.group.service;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskDetailResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupTaskListResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.task.entity.Task;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskTakeoverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupTaskQueryService {

    private final TaskOccurrenceRepository occurrenceRepository;
    private final TaskTakeoverRepository takeoverRepository;

    public GroupTaskListResponse getGroupTasks(Long groupId, LocalDate selectedDate) {
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

        return GroupTaskListResponse.builder()
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .weekDates(weekDates)
                .selectedDate(selectedDate)
                .items(items)
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
