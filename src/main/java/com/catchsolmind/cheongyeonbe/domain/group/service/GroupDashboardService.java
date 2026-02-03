package com.catchsolmind.cheongyeonbe.domain.group.service;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupDashboardResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskPostponeLogRepository;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupDashboardService {

    private final TaskOccurrenceRepository occurrenceRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TaskPostponeLogRepository postponeLogRepository;

    public GroupDashboardResponse getDashboard(Long groupId) {
        LocalDate today = LocalDate.now();

        // 이번 달 연속 청소 일수 계산
        Integer streakDays = calculateMonthlyStreakDays(groupId, today);

        // 이번 주 청소왕 계산
        GroupDashboardResponse.CleaningKingDto cleaningKing = calculateWeeklyCleaningKing(groupId, today);

        // 집안일 완료율 계산 (이번 주 기준)
        Double completionRate = calculateWeeklyCompletionRate(groupId, today);

        // 미루기 TOP 3
        List<GroupDashboardResponse.PostponeRankDto> postponeTop3 = calculatePostponeTop3(groupId);

        return GroupDashboardResponse.builder()
                .thisMonthStreakDays(streakDays)
                .thisWeekCleaningKing(cleaningKing)
                .houseworkCompletionRate(completionRate)
                .postponeTop3(postponeTop3)
                .build();
    }

    /**
     * 이번 달 연속 청소 일수 계산
     * - 오늘부터 역순으로, 그룹 내 하루라도 완료된 할일이 있으면 streak 카운트
     */
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
                break; // 연속이 끊기면 중단
            }
        }

        return streakDays;
    }

    /**
     * 이번 주 청소왕 - 이번 주 가장 많이 완료한 멤버
     */
    private GroupDashboardResponse.CleaningKingDto calculateWeeklyCleaningKing(Long groupId, LocalDate today) {
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<TaskOccurrence> weekOccurrences = occurrenceRepository.findByGroup_GroupIdAndOccurDateBetween(
                groupId, weekStart, weekEnd
        );

        // 멤버별 완료 수 집계
        Map<Long, Long> memberCompletedCount = weekOccurrences.stream()
                .filter(occ -> occ.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        occ -> occ.getPrimaryAssignedMember().getGroupMemberId(),
                        Collectors.counting()
                ));

        if (memberCompletedCount.isEmpty()) {
            return null;
        }

        // 가장 많이 완료한 멤버 찾기
        Map.Entry<Long, Long> topEntry = memberCompletedCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        if (topEntry == null) {
            return null;
        }

        // 해당 멤버 정보 조회
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

    /**
     * 이번 주 집안일 완료율 (%)
     */
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

        return Math.round((double) completedCount / totalCount * 1000.0) / 10.0; // 소수점 1자리
    }

    /**
     * 미루기 TOP 3 - 그룹 내 미루기 횟수가 많은 멤버 상위 3명
     */
    private List<GroupDashboardResponse.PostponeRankDto> calculatePostponeTop3(Long groupId) {
        List<GroupMember> members = groupMemberRepository.findByGroup_GroupIdAndStatusNot(
                groupId, MemberStatus.LEFT
        );

        // 멤버별 미루기 횟수 집계
        List<GroupDashboardResponse.PostponeRankDto> allMembers = members.stream()
                .map(member -> {
                    long postponeCount = postponeLogRepository.countByMember_GroupMemberId(member.getGroupMemberId());
                    return GroupDashboardResponse.PostponeRankDto.builder()
                            .memberId(member.getGroupMemberId())
                            .nickname(member.getUser().getNickname())
                            .profileImageUrl(member.getUser().getProfileImg())
                            .postponeCount(postponeCount)
                            .build();
                })
                .sorted(Comparator.comparingLong(GroupDashboardResponse.PostponeRankDto::getPostponeCount).reversed())
                .limit(3)
                .collect(Collectors.toList());

        // rank 설정
        List<GroupDashboardResponse.PostponeRankDto> rankedList = new ArrayList<>();
        for (int i = 0; i < allMembers.size(); i++) {
            GroupDashboardResponse.PostponeRankDto dto = allMembers.get(i);
            rankedList.add(GroupDashboardResponse.PostponeRankDto.builder()
                    .rank(i + 1)
                    .memberId(dto.getMemberId())
                    .nickname(dto.getNickname())
                    .profileImageUrl(dto.getProfileImageUrl())
                    .postponeCount(dto.getPostponeCount())
                    .build());
        }

        return rankedList;
    }
}
