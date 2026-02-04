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
import com.catchsolmind.cheongyeonbe.global.enums.TaskSubCategory;
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

    private static final int DEFAULT_CUSTOM_TASK_POINT = 10;

    public TaskTypeListResponse getTaskTypesByCategory(TaskCategory category, TaskSubCategory subCategory) {

        List<TaskType> taskTypes;

        if (category == null) {
            taskTypes = taskTypeRepository.findAll();
        } else if (category == TaskCategory.ETC && subCategory != null) {
            taskTypes = taskTypeRepository.findByCategoryAndSubCategory(category, subCategory);
        } else {
            taskTypes = taskTypeRepository.findByCategory(category);
        }

        List<TaskTypeListResponse.TaskTypeItemDto> items = taskTypes.stream()
                .map(t -> TaskTypeListResponse.TaskTypeItemDto.builder()
                        .taskTypeId(t.getTaskTypeId())
                        .category(t.getCategory())
                        .subCategory(t.getSubCategory())
                        .name(t.getName())
                        .point(t.getPoint())
                        .isFavorite(false)
                        .build())
                .toList();

        return TaskTypeListResponse.builder()
                .items(items)
                .build();
    }

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
                .map(t -> TaskTypeListResponse.TaskTypeItemDto.builder()
                        .taskTypeId(t.getTaskTypeId())
                        .category(t.getCategory())
                        .subCategory(t.getSubCategory())
                        .name(t.getName())
                        .point(t.getPoint())
                        .isFavorite(true) // 즐겨찾기 목록이니까 전부 true
                        .build())
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
}
