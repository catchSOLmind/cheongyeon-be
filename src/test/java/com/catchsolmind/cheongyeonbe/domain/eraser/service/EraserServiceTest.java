package com.catchsolmind.cheongyeonbe.domain.eraser.service;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.RecommendationResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.SuggestionTask;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.SuggestionTaskRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.Task;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskLogRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.global.enums.SuggestionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EraserServiceTest {
    @InjectMocks
    private EraserService eraserService;

    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private SuggestionTaskRepository suggestionTaskRepository;
    @Mock
    private TaskOccurrenceRepository taskOccurrenceRepository;
    @Mock
    private TaskLogRepository taskLogRepository;

    @Test
    @DisplayName("상황 1: 일정에 있는데 3번 이상 미뤘으면 추천 목록에 떠야 한다.")
    void recommendWhenPostponed3Times() {
        // given
        Long userId = 1L;
        Group group = Group.builder().groupId(100L).build();
        TaskType taskType = TaskType.builder().taskTypeId(1L).build();

        // 고정된 상품 데이터 (냉장고 청소)
        SuggestionTask product = SuggestionTask.builder()
                .suggestionTaskId(1L)
                .taskType(taskType)
                .title("냉장고 청소")
                .defaultEstimatedMinutes(120) // 필수값
                .descDelayed("미루지 마세요")
                .build();

        // 유저의 일정 (3번 미룸)
        TaskOccurrence occurrence = TaskOccurrence.builder()
                .task(Task.builder().taskType(taskType).build())
                .postponeCount(3) // 핵심 조건
                .occurDate(LocalDate.now())
                .build();

        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));
        given(suggestionTaskRepository.findAll()).willReturn(List.of(product));
        given(taskOccurrenceRepository.findUnfinishedByGroupId(anyLong())).willReturn(List.of(occurrence));
        given(taskLogRepository.findLastDoneDatesByGroupAndTaskTypes(anyLong(), anyList()))
                .willReturn(Collections.emptyList());

        // when
        List<RecommendationResponse> results = eraserService.getRecommendations(userId);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("냉장고 청소");
        assertThat(results.get(0).tags()).contains(SuggestionType.DELAYED);
    }

    @Test
    @DisplayName("상황 2: 일정에 없지만, 마지막으로 한 지 주기가 지났으면 추천에 떠야 한다.")
    void recommendWhenCyclePassed() {
        // given
        Long userId = 1L;
        Group group = Group.builder().groupId(100L).build();
        TaskType taskType = TaskType.builder().taskTypeId(2L).build();

        // 고정된 상품 데이터 (화장실 청소, 주기 7일)
        SuggestionTask product = SuggestionTask.builder()
                .suggestionTaskId(2L)
                .taskType(taskType)
                .title("화장실 청소")
                .defaultEstimatedMinutes(60) // [수정] NPE 방지를 위해 필수값 추가
                .recommendationCycleDays(7) // 7일 주기
                .descNoAssignee("{task_name} 담당자가 없네요.") // 무담당 멘트 템플릿
                .build();

        // 유저의 마지막 기록 (10일 전) -> 7일 지났으니 추천 대상!
        LocalDateTime lastDoneDate = LocalDateTime.now().minusDays(10);
        Object[] logRow = {2L, lastDoneDate};

        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));
        given(suggestionTaskRepository.findAll()).willReturn(List.of(product));
        given(taskOccurrenceRepository.findUnfinishedByGroupId(anyLong())).willReturn(List.of()); // 현재 일정 없음
        given(taskLogRepository.findLastDoneDatesByGroupAndTaskTypes(anyLong(), anyList()))
                .willReturn(Collections.singletonList(logRow));

        // when
        List<RecommendationResponse> results = eraserService.getRecommendations(userId);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("화장실 청소");
        // 주기가 지났으므로 '무담당(NO_ASSIGNEE)' 태그가 붙어야 함
        assertThat(results.get(0).tags()).contains(SuggestionType.NO_ASSIGNEE);
    }

    @Test
    @DisplayName("상황 3: 일정에 없고, 주기도 아직 안 지났으면 추천 안 함")
    void do_not_recommend_when_cycle_not_passed() {
        // given
        Long userId = 1L;
        Group group = Group.builder().groupId(100L).build();
        TaskType taskType = TaskType.builder().taskTypeId(2L).build();

        // 고정된 상품 데이터 (화장실 청소, 주기 7일)
        SuggestionTask product = SuggestionTask.builder()
                .suggestionTaskId(2L)
                .taskType(taskType)
                .defaultEstimatedMinutes(60) // [수정] NPE 방지를 위해 필수값 추가
                .recommendationCycleDays(7)
                .build();

        // 유저의 마지막 기록 (3일 전) -> 아직 주기 안 됨
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        Object[] logRow = {2L, threeDaysAgo};

        given(groupMemberRepository.findGroupByUserId(userId)).willReturn(Optional.of(group));
        given(suggestionTaskRepository.findAll()).willReturn(List.of(product));
        given(taskOccurrenceRepository.findUnfinishedByGroupId(anyLong())).willReturn(List.of());
        given(taskLogRepository.findLastDoneDatesByGroupAndTaskTypes(anyLong(), anyList()))
                .willReturn(Collections.singletonList(logRow));

        // when
        List<RecommendationResponse> results = eraserService.getRecommendations(userId);

        // then
        assertThat(results).isEmpty(); // 추천 목록이 비어있어야 함
    }
}