package com.catchsolmind.cheongyeonbe.domain.task.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberFavoriteTaskType;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.repository.MemberFavoriteTaskTypeRepository;
import com.catchsolmind.cheongyeonbe.domain.task.dto.request.TaskTypeCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeFavoriteResponse;
import com.catchsolmind.cheongyeonbe.domain.task.dto.response.TaskTypeListResponse;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskTypeRepository;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private static final int DEFAULT_CUSTOM_TASK_POINT = 10;

    /**
     * 세부 업무 조회
     * - category: 카테고리 필터 (선택)
     * - favorite: true면 즐겨찾기만 조회
     * - q: 검색어 (선택)
     */
    public TaskTypeListResponse getTaskTypes(Long memberId, TaskCategory category, Boolean favorite, String q) {

        List<TaskType> taskTypes;

        // 즐겨찾기만 조회하는 경우
        if (Boolean.TRUE.equals(favorite)) {
            List<MemberFavoriteTaskType> favorites = favoriteRepository.findAllByMember_GroupMemberId(memberId);
            List<Long> favoriteTaskTypeIds = favorites.stream()
                    .map(f -> f.getTaskType().getTaskTypeId())
                    .collect(Collectors.toList());

            if (favoriteTaskTypeIds.isEmpty()) {
                return TaskTypeListResponse.builder()
                        .items(List.of())
                        .build();
            }

            taskTypes = taskTypeRepository.findByTaskTypeIdIn(favoriteTaskTypeIds);

            // 카테고리 필터 적용
            if (category != null) {
                taskTypes = taskTypes.stream()
                        .filter(t -> t.getCategory() == category)
                        .collect(Collectors.toList());
            }

            // 검색어 필터 적용
            if (q != null && !q.isBlank()) {
                String searchKeyword = q.toLowerCase();
                taskTypes = taskTypes.stream()
                        .filter(t -> t.getName().toLowerCase().contains(searchKeyword))
                        .collect(Collectors.toList());
            }

        } else {
            // 일반 조회
            if (category != null && q != null && !q.isBlank()) {
                taskTypes = taskTypeRepository.findByCategoryAndNameContaining(category, q);
            } else if (category != null) {
                taskTypes = taskTypeRepository.findByCategory(category);
            } else if (q != null && !q.isBlank()) {
                taskTypes = taskTypeRepository.findByNameContaining(q);
            } else {
                taskTypes = taskTypeRepository.findAll();
            }
        }

        // 즐겨찾기 여부 확인을 위한 Set
        Set<Long> favoriteTaskTypeIds = favoriteRepository.findAllByMember_GroupMemberId(memberId)
                .stream()
                .map(f -> f.getTaskType().getTaskTypeId())
                .collect(Collectors.toSet());

        List<TaskTypeListResponse.TaskTypeItemDto> items = taskTypes.stream()
                .map(t -> TaskTypeListResponse.TaskTypeItemDto.builder()
                        .taskTypeId(t.getTaskTypeId())
                        .category(t.getCategory())
                        .subCategory(t.getSubCategory())
                        .name(t.getName())
                        .point(t.getPoint())
                        .isFavorite(favoriteTaskTypeIds.contains(t.getTaskTypeId()))
                        .build())
                .collect(Collectors.toList());

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
}
