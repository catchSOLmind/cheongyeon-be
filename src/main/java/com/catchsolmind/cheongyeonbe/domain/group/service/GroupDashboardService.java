package com.catchsolmind.cheongyeonbe.domain.group.service;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupDashboardResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskLogRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupDashboardService {

    private final TaskOccurrenceRepository occurrenceRepository;
    private final TaskLogRepository taskLogRepository;

    public GroupDashboardResponse getDashboard(Long groupId) {
        LocalDate today = LocalDate.now();

        Integer streakDays = calculateMonthlyStreakDays(groupId, today);
        GroupDashboardResponse.CleaningKingDto cleaningKing = calculateWeeklyCleaningKing(groupId, today);
        Double completionRate = calculateWeeklyCompletionRate(groupId, today);
        List<GroupDashboardResponse.PostponeRankDto> postponeTop3 = calculatePostponeTop3(groupId);

        return GroupDashboardResponse.builder()
                .thisMonthStreakDays(streakDays)
                .thisWeekCleaningKing(cleaningKing)
                .houseworkCompletionRate(completionRate)
                .postponeTop3(postponeTop3)
                .build();
    }

    private Integer calculateMonthlyStreakDays(Long groupId, LocalDate today) {
        LocalDate monthStart = today.withDayOfMonth(1);
        int streakDays = 0;

        for (LocalDate date = today; !date.isBefore(monthStart); date = date.minusDays(1)) {
            long completedCount = occurrenceRepository.countByGroup_GroupIdAndOccurDateBetweenAndStatus(
                    groupId, date, date, TaskStatus.COMPLETED
            );
            if (completedCount > 0) {
                streakDays++;
            } else {
                break;
            }
        }
        return streakDays;
    }

    private GroupDashboardResponse.CleaningKingDto calculateWeeklyCleaningKing(Long groupId, LocalDate today) {
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<TaskOccurrence> weekOccurrences = occurrenceRepository.findByGroup_GroupIdAndOccurDateBetween(
                groupId, weekStart, weekEnd
        );

        Map<Long, Long> memberCompletedCount = weekOccurrences.stream()
                .filter(occ -> occ.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        occ -> occ.getPrimaryAssignedMember().getGroupMemberId(),
                        Collectors.counting()
                ));

        if (memberCompletedCount.isEmpty()) {
            return null;
        }

        Map.Entry<Long, Long> topEntry = memberCompletedCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (topEntry == null) {
            return null;
        }

        GroupMember topMember = weekOccurrences.stream()
                .filter(occ -> occ.getPrimaryAssignedMember().getGroupMemberId().equals(topEntry.getKey()))
                .findFirst()
                .map(TaskOccurrence::getPrimaryAssignedMember)
                .orElse(null);

        if (topMember == null) {
            return null;
        }

        return GroupDashboardResponse.CleaningKingDto.builder()
                .memberId(topMember.getGroupMemberId())
                .nickname(topMember.getUser().getNickname())
                .profileImageUrl(topMember.getUser().getProfileImg())
                .completedCount(topEntry.getValue().intValue())
                .build();
    }

    private Double calculateWeeklyCompletionRate(Long groupId, LocalDate today) {
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        long totalCount = occurrenceRepository.countByGroup_GroupIdAndOccurDateBetween(
                groupId, weekStart, weekEnd
        );

        if (totalCount == 0) {
            return 0.0;
        }

        long completedCount = occurrenceRepository.countByGroup_GroupIdAndOccurDateBetweenAndStatus(
                groupId, weekStart, weekEnd, TaskStatus.COMPLETED
        );

        return Math.round((double) completedCount / totalCount * 1000.0) / 10.0;
    }

    private List<GroupDashboardResponse.PostponeRankDto> calculatePostponeTop3(Long groupId) {
        List<Object[]> results = taskLogRepository.findCategoryRankStats(groupId, PageRequest.of(0, 3));

        List<GroupDashboardResponse.PostponeRankDto> rankList = new ArrayList<>();
        int rank = 1;

        for (Object[] row : results) {
            TaskCategory category = (TaskCategory) row[0]; // 카테고리
            Long count = (Long) row[1]; // 횟수

            rankList.add(GroupDashboardResponse.PostponeRankDto.builder()
                    .rank(rank++) // 순위 (1, 2, 3...)
                    .memberId(null) // null 고정
                    .nickname(category.name()) // 카테고리 이름을 닉네임 필드에 매핑
                    .profileImageUrl(null) // null 고정
                    .postponeCount(count)// 완료 횟수를 postponeCount 필드에 매핑
                    .build());
        }

        return rankList;
    }
}
