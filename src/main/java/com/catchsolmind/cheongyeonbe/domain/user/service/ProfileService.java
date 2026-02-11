package com.catchsolmind.cheongyeonbe.domain.user.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberPreference;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestResultRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.domain.user.dto.*;
import com.catchsolmind.cheongyeonbe.domain.user.dto.request.ProfileUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileGetResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileUpdateResponse;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.MemberPreferenceRepository;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final MemberPreferenceRepository memberPreferenceRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TaskOccurrenceRepository taskOccurrenceRepository;
    private final HouseworkTestResultRepository houseworkTestResultRepository;

    public ProfileGetResponse getProfile(User user) {
        // 현재 그룹 멤버 조회
        GroupMember member = groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                .orElse(null);

        Long groupId = null;
        String type = null;
        String typeLabel = null;
        boolean hasCompletedTest = false;

        // Summary 기본값
        int streakDays = 0;
        int totalPoints = 0;
        int completedTaskCount = 0;

        // MonthlyActivity 기본값
        YearMonth currentMonth = YearMonth.now();
        int monthlyTotalCount = 0;
        List<ProfileGetResponse.CategoryActivity> categories = Collections.emptyList();

        // 성향 정보: housework_test 테이블에서 User 기준으로 조회
        HouseworkTestResult testResult = houseworkTestResultRepository.findByUser(user).orElse(null);
        if (testResult != null && testResult.getResultType() != null) {
            type = testResult.getResultType().name();
            typeLabel = testResult.getResultType().getTitle();
            hasCompletedTest = true;
        }

        if (member != null) {
            groupId = member.getGroup().getGroupId();

            // 이번 달 성과 계산
            LocalDate monthStart = currentMonth.atDay(1);
            LocalDate monthEnd = currentMonth.atEndOfMonth();
            LocalDate today = LocalDate.now();

            // 내 이번 달 완료 Task 조회
            List<TaskOccurrence> myMonthlyOccurrences = taskOccurrenceRepository
                    .findByGroup_GroupIdAndPrimaryAssignedMember_GroupMemberIdAndOccurDateBetween(
                            groupId, member.getGroupMemberId(), monthStart, monthEnd
                    );

            // 완료된 Task 수
            List<TaskOccurrence> myCompletedOccurrences = myMonthlyOccurrences.stream()
                    .filter(occ -> occ.getStatus() == TaskStatus.COMPLETED)
                    .collect(Collectors.toList());
            completedTaskCount = myCompletedOccurrences.size();

            // 총 포인트: User.pointBalance (적립 - 사용 반영된 실제 잔액)
            totalPoints = (user.getPointBalance() != null) ? user.getPointBalance() : 0;

            // 연속 달성 일수 계산 (오늘부터 역순으로)
            streakDays = calculateStreakDays(member.getGroupMemberId(), groupId, today, monthStart);

            // 이번 달 활동 (카테고리별)
            monthlyTotalCount = myCompletedOccurrences.size();
            categories = calculateCategoryActivity(myCompletedOccurrences, groupId, monthStart, monthEnd);
        }

        return ProfileGetResponse.builder()
                .profile(ProfileGetResponse.Profile.builder()
                        .userId(user.getUserId())
                        .groupId(groupId)
                        .nickname(user.getNickname())
                        .profileImageUrl(user.getProfileImg())
                        .build())
                .personalityInfo(ProfileGetResponse.PersonalityInfo.builder()
                        .hasCompleted(hasCompletedTest)
                        .houseworkType(type)
                        .houseworkTypeLabel(typeLabel)
                        .build())
                .summary(ProfileGetResponse.Summary.builder()
                        .streakDays(streakDays)
                        .totalPoints(totalPoints)
                        .completedTaskCount(completedTaskCount)
                        .build())
                .monthlyActivity(ProfileGetResponse.MonthlyActivity.builder()
                        .month(currentMonth.toString())
                        .totalCount(monthlyTotalCount)
                        .categories(categories)
                        .build())
                .build();
    }

    /**
     * 연속 달성 일수 계산
     * - 오늘부터 역순으로 하루씩 체크
     * - 해당 날짜에 내가 완료한 Task가 있으면 streak++
     * - 없으면 중단
     */
    private int calculateStreakDays(Long memberId, Long groupId, LocalDate today, LocalDate monthStart) {
        int streak = 0;

        for (LocalDate date = today; !date.isBefore(monthStart); date = date.minusDays(1)) {
            List<TaskOccurrence> dayOccurrences = taskOccurrenceRepository
                    .findByGroup_GroupIdAndPrimaryAssignedMember_GroupMemberIdAndOccurDateBetween(
                            groupId, memberId, date, date
                    );

            boolean hasCompletedToday = dayOccurrences.stream()
                    .anyMatch(occ -> occ.getStatus() == TaskStatus.COMPLETED);

            if (hasCompletedToday) {
                streak++;
            } else {
                break;
            }
        }

        return streak;
    }

    /**
     * 카테고리별 활동 계산
     * - 내가 완료한 Task를 카테고리별로 그룹핑
     * - 그룹 전체 대비 내 비율 계산
     */
    private List<ProfileGetResponse.CategoryActivity> calculateCategoryActivity(
            List<TaskOccurrence> myCompletedOccurrences,
            Long groupId,
            LocalDate monthStart,
            LocalDate monthEnd
    ) {
        if (myCompletedOccurrences.isEmpty()) {
            return Collections.emptyList();
        }

        // 내 카테고리별 완료 수
        Map<String, Long> myCategoryCount = myCompletedOccurrences.stream()
                .collect(Collectors.groupingBy(
                        occ -> occ.getTask().getTaskType().getCategory().name(),
                        Collectors.counting()
                ));

        // 그룹 전체 이번 달 완료 Task 조회
        List<TaskOccurrence> groupMonthlyCompleted = taskOccurrenceRepository
                .findByGroup_GroupIdAndOccurDateBetween(groupId, monthStart, monthEnd)
                .stream()
                .filter(occ -> occ.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toList());

        // 그룹 카테고리별 완료 수
        Map<String, Long> groupCategoryCount = groupMonthlyCompleted.stream()
                .collect(Collectors.groupingBy(
                        occ -> occ.getTask().getTaskType().getCategory().name(),
                        Collectors.counting()
                ));

        // 카테고리별 활동 생성
        return myCategoryCount.entrySet().stream()
                .map(entry -> {
                    String category = entry.getKey();
                    int myCount = entry.getValue().intValue();
                    long groupCount = groupCategoryCount.getOrDefault(category, 1L);

                    int mySharePercent = (int) Math.round((double) myCount / groupCount * 100);

                    return ProfileGetResponse.CategoryActivity.builder()
                            .categoryName(getCategoryDisplayName(category))
                            .count(myCount)
                            .mySharePercent(mySharePercent)
                            .build();
                })
                .sorted(Comparator.comparingInt(ProfileGetResponse.CategoryActivity::getCount).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 표시명 변환
     */
    private String getCategoryDisplayName(String category) {
        return switch (category) {
            case "KITCHEN" -> "주방";
            case "BATHROOM" -> "화장실";
            case "LIVINGROOM" -> "거실";
            case "BEDROOM" -> "침실";
            case "LAUNDRY" -> "세탁";
            case "ETC" -> "기타";
            default -> category;
        };
    }

    public ProfileGetResponse getProfile(User user, GroupMember member) {
        return getProfile(user);
    }

    public ProfileUpdateResponse updateProfile(User user, GroupMember member, ProfileUpdateRequest request) {

        String type = null;

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }

        userRepository.save(user);

        // member가 null이면 현재 그룹 멤버 조회
        GroupMember currentMember = member;
        if (currentMember == null) {
            currentMember = groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                    .orElse(null);
        }

        // 성향 테스트는 member 가 존재할때 수행
        if (request.getHouseworkType() != null && currentMember != null) {
            final GroupMember finalMember = currentMember;
            MemberPreference pref = memberPreferenceRepository.findByMember(currentMember)
                    .orElseGet(() -> memberPreferenceRepository.save(
                            MemberPreference.builder()
                                    .member(finalMember)
                                    .build()
                    ));

            pref.setPersonalityType(request.getHouseworkType());
            memberPreferenceRepository.save(pref);
            type = pref.getPersonalityType();
        }

        return ProfileUpdateResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImg())
                .houseworkType(type)
                .houseworkTypeLabel(HouseworkTypeMapper.labelOf(type))
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
