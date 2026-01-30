package com.catchsolmind.cheongyeonbe.domain.task.service;

import com.catchsolmind.cheongyeonbe.domain.task.dto.data.MyTaskItemDto;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskDetailResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.MyTaskListResponse;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskTakeoverRepository;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

        List<LocalDate> weekDates = weekStart.datesUntil(weekEnd.plusDays(1)).collect(Collectors.toList());

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
                            .point(occ.getTask().getTaskType().getPoint())   // ✅ point
                            .time(null)
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

        boolean isTakeover = takeoverRepository.existsByOccurrence_OccurrenceId(occurrenceId);

        String repeatRule = occ.getTask().getRepeatRule();
        boolean repeatEnabled = repeatRule != null && !repeatRule.isBlank();

        return MyTaskDetailResponse.builder()
                .occurrenceId(occ.getOccurrenceId())
                .taskId(occ.getTask().getTaskId())
                .taskTypeId(occ.getTask().getTaskType().getTaskTypeId())
                .category(occ.getTask().getTaskType().getCategory())
                .taskName(occ.getTask().getTaskType().getName())
                .point(occ.getTask().getTaskType().getPoint())
                .date(occ.getOccurDate())
                .time(null)
                .repeatEnabled(repeatEnabled)
                .repeatRule(repeatRule)
                .primaryAssignedMemberId(occ.getPrimaryAssignedMember().getGroupMemberId())
                .status(occ.getStatus())
                .isTakeover(isTakeover)
                .build();
    }
}
