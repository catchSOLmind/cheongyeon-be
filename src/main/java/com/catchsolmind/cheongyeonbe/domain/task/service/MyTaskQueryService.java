package com.catchsolmind.cheongyeonbe.domain.task.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.task.dto.data.MyTaskItemDto;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskDetailResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskListResponse;
import com.catchsolmind.cheongyeonbe.domain.task.entity.Task;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskTakeoverRepository;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
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
public class MyTaskQueryService {

    private final TaskOccurrenceRepository occurrenceRepository;
    private final TaskTakeoverRepository takeoverRepository;

    public MyTaskListResponse getMyTasks(Long groupId, Long myMemberId, LocalDate selectedDate) {
        LocalDate weekStart = selectedDate.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<LocalDate> weekDates = weekStart.datesUntil(weekEnd.plusDays(1))
                .collect(Collectors.toList());

        List<TaskOccurrence> occurrences =
                occurrenceRepository.findByGroup_GroupIdAndPrimaryAssignedMember_GroupMemberIdAndOccurDate(
                        groupId, myMemberId, selectedDate
                );

        List<MyTaskItemDto> items = occurrences.stream()
                .map(occ -> {
                    boolean isTakeover = takeoverRepository.existsByOccurrence_OccurrenceId(occ.getOccurrenceId());
                    return MyTaskItemDto.builder()
                            .occurrenceId(occ.getOccurrenceId())
                            .taskId(occ.getTask().getTaskId())
                            .taskTypeId(occ.getTask().getTaskType().getTaskTypeId())
                            .taskName(occ.getTask().getTaskType().getName())
                            .point(occ.getTask().getTaskType().getPoint())
                            .time(occ.getTask().getTime())
                            .status(TaskStatus.valueOf(occ.getStatus().name()))
                            .isTakeover(isTakeover)
                            .primaryAssignedMemberId(occ.getPrimaryAssignedMember().getGroupMemberId())
                            .build();
                })
                .collect(Collectors.toList());

        return MyTaskListResponse.builder()
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .weekDates(weekDates)
                .selectedDate(selectedDate)
                .items(items)
                .build();
    }

    public MyTaskDetailResponse getMyTaskDetail(Long groupId, Long occurrenceId) {
        TaskOccurrence occ = occurrenceRepository.findByOccurrenceIdAndGroup_GroupId(occurrenceId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("Occurrence not found"));

        Task task = occ.getTask();
        String repeatRule = task.getRepeatRule();
        boolean repeatEnabled = repeatRule != null && !repeatRule.isBlank();
        List<String> daysOfWeek = parseRRuleToDaysOfWeek(repeatRule);

        GroupMember assignee = occ.getPrimaryAssignedMember();

        return MyTaskDetailResponse.builder()
                .occurrenceId(occ.getOccurrenceId())
                .taskId(task.getTaskId())
                .groupId(groupId)
                .taskType(MyTaskDetailResponse.TaskTypeDto.builder()
                        .taskTypeId(task.getTaskType().getTaskTypeId())
                        .category(task.getTaskType().getCategory())
                        .name(task.getTaskType().getName())
                        .build())
                .date(occ.getOccurDate().toString())
                .time(task.getTime())
                .repeat(MyTaskDetailResponse.RepeatDto.builder()
                        .enabled(repeatEnabled)
                        .daysOfWeek(daysOfWeek)
                        .build())
                .assignee(MyTaskDetailResponse.AssigneeDto.builder()
                        .memberId(assignee.getGroupMemberId())
                        .nickname(assignee.getUser().getNickname())
                        .profileImageUrl(assignee.getUser().getProfileImg())
                        .build())
                .status(occ.getStatus())
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