package com.catchsolmind.cheongyeonbe.domain.task.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberFavoriteTaskType;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.repository.MemberFavoriteTaskTypeRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestResultRepository;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.TaskTypeCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeFavoriteResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeListResponse;
import com.catchsolmind.cheongyeonbe.domain.task.entity.PersonalityRecommendTask;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.domain.task.repository.PersonalityRecommendTaskRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskTypeRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.enums.RecommendationType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory;
import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskTypeService {

    private final TaskTypeRepository taskTypeRepository;
    private final MemberFavoriteTaskTypeRepository favoriteRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final PersonalityRecommendTaskRepository personalityRecommendTaskRepository;
    private final HouseworkTestResultRepository houseworkTestResultRepository;

    private static final int DEFAULT_CUSTOM_TASK_POINT = 10;

    // 새해맞이 추천이 있는 카테고리
    private static final Set<TaskCategory> NEW_YEAR_CATEGORIES = Set.of(
            TaskCategory.BATHROOM, TaskCategory.KITCHEN, TaskCategory.BEDROOM, TaskCategory.LIVING
    );

    /**
     * 카테고리별 세부 업무 조회 (섹션별 분류 포함)
     */
    public TaskTypeListResponse getTaskTypesByCategory(
            TaskCategory category, TaskSubCategory subCategory, User user) {

        // 1. 기존 업무 조회 (recommendation_type = NONE)
        List<TaskType> defaultTaskTypes;
        if (category == null) {
            defaultTaskTypes = taskTypeRepository.findAll();
        } else if (category == TaskCategory.ETC && subCategory != null) {
            defaultTaskTypes = taskTypeRepository.findByCategoryAndSubCategoryAndRecommendationType(
                    category, subCategory, RecommendationType.NONE);
        } else {
            defaultTaskTypes = taskTypeRepository.findByCategoryAndRecommendationType(
                    category, RecommendationType.NONE);
        }

        // 즐겨찾기 ID 조회
        Set<Long> favoriteIds = Collections.emptySet();
        Long memberId = getMemberIdIfExists(user);
        if (memberId != null) {
            favoriteIds = favoriteRepository.findAllByMember_GroupMemberId(memberId).stream()
                    .map(f -> f.getTaskType().getTaskTypeId())
                    .collect(Collectors.toSet());
        }

        Set<Long> finalFavoriteIds = favoriteIds;

        List<TaskTypeListResponse.TaskTypeItemDto> items = defaultTaskTypes.stream()
                .map(t -> toDto(t, finalFavoriteIds.contains(t.getTaskTypeId())))
                .toList();

        // 2. 새해맞이 추천 업무 (해당 카테고리만)
        List<TaskTypeListResponse.TaskTypeItemDto> newYearItems = List.of();
        if (category != null && NEW_YEAR_CATEGORIES.contains(category)) {
            List<TaskType> newYearTaskTypes = taskTypeRepository.findByCategoryAndRecommendationType(
                    category, RecommendationType.NEW_YEAR);
            newYearItems = newYearTaskTypes.stream()
                    .map(t -> toDto(t, finalFavoriteIds.contains(t.getTaskTypeId())))
                    .toList();
        }

        // 3. 사용자 성향별 추천 업무
        List<TaskTypeListResponse.TaskTypeItemDto> personalityItems = List.of();
        String personalityLabel = null;

        if (category != null && user != null) {
            // 기타-그외(OTHER)는 성향 추천 없음
            boolean isEtcOther = (category == TaskCategory.ETC && subCategory == TaskSubCategory.OTHER);
            if (!isEtcOther) {
                TestResultType personalityType = getUserPersonalityType(user);
                if (personalityType != null) {
                    personalityLabel = personalityType.getTitle();

                    List<PersonalityRecommendTask> recommends;
                    if (category == TaskCategory.ETC && subCategory != null) {
                        recommends = personalityRecommendTaskRepository
                                .findByPersonalityTypeAndCategoryAndSubCategory(personalityType, category, subCategory);
                    } else {
                        recommends = personalityRecommendTaskRepository
                                .findByPersonalityTypeAndCategory(personalityType, category);
                    }

                    personalityItems = recommends.stream()
                            .map(r -> toDto(r.getTaskType(), finalFavoriteIds.contains(r.getTaskType().getTaskTypeId())))
                            .toList();
                }
            }
        }

        return TaskTypeListResponse.builder()
                .items(items)
                .newYearItems(newYearItems.isEmpty() ? null : newYearItems)
                .personalityItems(personalityItems.isEmpty() ? null : personalityItems)
                .personalityLabel(personalityLabel)
                .build();
    }

    /**
     * 즐겨찾기 세부 업무 조회
     */
    public TaskTypeListResponse getFavoriteTaskTypes(Long memberId) {
        List<MemberFavoriteTaskType> favorites = favoriteRepository.findAllByMember_GroupMemberId(memberId);
        List<Long> favoriteTaskTypeIds = favorites.stream()
                .map(f -> f.getTaskType().getTaskTypeId())
                .toList();

        if (favoriteTaskTypeIds.isEmpty()) {
            return TaskTypeListResponse.builder()
                    .items(List.of())
                    .build();
        }

        List<TaskType> taskTypes = taskTypeRepository.findByTaskTypeIdIn(favoriteTaskTypeIds);

        List<TaskTypeListResponse.TaskTypeItemDto> items = taskTypes.stream()
                .map(t -> toDto(t, true))
                .toList();

        return TaskTypeListResponse.builder()
                .items(items)
                .build();
    }

    /**
     * 세부 업무 등록 (사용자 직접 등록)
     */
    @Transactional
    public TaskTypeCreateResponse createTaskType(TaskTypeCreateRequest request) {
        TaskType taskType = TaskType.builder()
                .category(request.getCategory())
                .subCategory(request.getSubCategory())
                .name(request.getName())
                .point(DEFAULT_CUSTOM_TASK_POINT)
                .build();

        taskTypeRepository.save(taskType);

        return TaskTypeCreateResponse.builder()
                .taskTypeId(taskType.getTaskTypeId())
                .category(taskType.getCategory())
                .subCategory(taskType.getSubCategory())
                .name(taskType.getName())
                .build();
    }

    /**
     * 즐겨찾기 추가
     */
    @Transactional
    public TaskTypeFavoriteResponse addFavorite(Long memberId, Long taskTypeId) {
        GroupMember member = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        TaskType taskType = taskTypeRepository.findById(taskTypeId)
                .orElseThrow(() -> new IllegalArgumentException("TaskType not found"));

        // 이미 즐겨찾기 되어있는지 확인
        if (favoriteRepository.existsByMember_GroupMemberIdAndTaskType_TaskTypeId(memberId, taskTypeId)) {
            return TaskTypeFavoriteResponse.builder()
                    .taskTypeId(taskTypeId)
                    .isFavorite(true)
                    .build();
        }

        MemberFavoriteTaskType favorite = MemberFavoriteTaskType.builder()
                .member(member)
                .taskType(taskType)
                .build();

        favoriteRepository.save(favorite);

        return TaskTypeFavoriteResponse.builder()
                .taskTypeId(taskTypeId)
                .isFavorite(true)
                .build();
    }

    /**
     * 즐겨찾기 해제
     */
    @Transactional
    public TaskTypeFavoriteResponse removeFavorite(Long memberId, Long taskTypeId) {
        favoriteRepository.findByMember_GroupMemberIdAndTaskType_TaskTypeId(memberId, taskTypeId)
                .ifPresent(favoriteRepository::delete);

        return TaskTypeFavoriteResponse.builder()
                .taskTypeId(taskTypeId)
                .isFavorite(false)
                .build();
    }

    // ===== Private Helper Methods =====

    private TaskTypeListResponse.TaskTypeItemDto toDto(TaskType t, boolean isFavorite) {
        return TaskTypeListResponse.TaskTypeItemDto.builder()
                .taskTypeId(t.getTaskTypeId())
                .category(t.getCategory())
                .subCategory(t.getSubCategory())
                .name(t.getName())
                .point(t.getPoint())
                .isFavorite(isFavorite)
                .build();
    }

    private TestResultType getUserPersonalityType(User user) {
        return houseworkTestResultRepository.findByUser(user)
                .map(HouseworkTestResult::getResultType)
                .orElse(null);
    }

    private Long getMemberIdIfExists(User user) {
        if (user == null) return null;
        return groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                .map(GroupMember::getGroupMemberId)
                .orElse(null);
    }
}
