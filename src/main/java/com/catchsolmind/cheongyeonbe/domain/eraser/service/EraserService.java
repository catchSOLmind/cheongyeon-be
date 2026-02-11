package com.catchsolmind.cheongyeonbe.domain.eraser.service;

import com.catchsolmind.cheongyeonbe.domain.eraser.dto.request.ReservationRequest;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.EraserTaskOptionsResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.ManagerCallResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.PaymentInfoResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.dto.response.RecommendationResponse;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.Reservation;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.ReservationItem;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.SuggestionTask;
import com.catchsolmind.cheongyeonbe.domain.eraser.entity.SuggestionTaskOption;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.ReservationItemRepository;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.ReservationRepository;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.SuggestionTaskOptionRepository;
import com.catchsolmind.cheongyeonbe.domain.eraser.repository.SuggestionTaskRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.point.entity.PointTransaction;
import com.catchsolmind.cheongyeonbe.domain.point.repository.PointTransactionRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskLogRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskOccurrenceRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.SuggestionType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import com.catchsolmind.cheongyeonbe.global.enums.TransactionType;
import com.catchsolmind.cheongyeonbe.global.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EraserService {
    public static final int MAX_USABLE_POINT = 20000;
    public static final String MANAGER_PROFILE_IMG = "/backend/profile/manager.png";

    private final GroupMemberRepository groupMemberRepository;
    private final TaskOccurrenceRepository taskOccurrenceRepository;
    private final SuggestionTaskRepository suggestionTaskRepository;
    private final TaskLogRepository taskLogRepository;
    private final UserRepository userRepository;
    private final SuggestionTaskOptionRepository suggestionTaskOptionRepository;
    private final ReservationRepository reservationRepository;
    private final PointTransactionRepository pointTransactionRepository;
    private final ReservationItemRepository reservationItemRepository;

    private final S3Properties s3Properties;

    public List<RecommendationResponse> getRecommendations(Long userId) {
        // 유저의 그룹 찾기
        Group group = groupMemberRepository.findGroupByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        List<TaskOccurrence> unfinishedTasks = taskOccurrenceRepository.findUnfinishedByGroupId(group.getGroupId());
        List<SuggestionTask> products = suggestionTaskRepository.findAll();

        List<Long> productTaskTypeIds = products.stream().map(p -> p.getTaskType().getTaskTypeId()).toList();
        List<Object[]> lastDoneLogs = taskLogRepository.findLastDoneDatesByGroupAndTaskTypes(group.getGroupId(), productTaskTypeIds);
        Map<Long, LocalDateTime> lastDoneMap = lastDoneLogs.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (LocalDateTime) row[1]));

        List<RecommendationResponse> recommendations = new ArrayList<>();

        // 상품별 추천 여부 검사
        for (SuggestionTask product : products) {
            TaskOccurrence matchedOccurrence = unfinishedTasks.stream()
                    .filter(o -> o.getTask().getTaskType().equals(product.getTaskType()))
                    .findFirst()
                    .orElse(null);

            boolean isSeason = isSeasonMatch(product);
            boolean shouldRecommend = false;
            List<SuggestionType> currentTags = new ArrayList<>();
            String description = "";

            // [Case A] 일정에 있는 경우 (미루기/지남) - 우선순위 최상
            if (matchedOccurrence != null) {
                boolean isDelayed = matchedOccurrence.getPostponeCount() > 0;
                boolean isOverdue = matchedOccurrence.getOccurDate().isBefore(LocalDate.now());

                if (isDelayed || isOverdue) {
                    shouldRecommend = true;
                    currentTags.add(SuggestionType.DELAYED);
                    long overdueDays = ChronoUnit.DAYS.between(matchedOccurrence.getOccurDate(), LocalDate.now());
                    String delayText;

                    if (overdueDays >= 1) {
                        delayText = overdueDays + "일";
                    } else {
                        delayText = matchedOccurrence.getPostponeCount() + "번";
                    }

                    description = product.getDescDelayed()
                            .replace("{delay_period}", delayText)
                            .replace("{time}", (product.getDefaultEstimatedMinutes() / 60) + "시간");
                }
            }

            // [Case B] 일정에 없는 경우
            else {
                // 시즌 여부를 가장 먼저 체크 (주기 설정 여부와 상관없이)
                if (isSeason) {
                    shouldRecommend = true;
                    currentTags.add(SuggestionType.GENERAL); // 시즌 태그
                    description = product.getDescGeneral()
                            .replace("{season}", getCurrentSeasonName());
                }

                // 시즌이 아니라면, 주기가 지났는지 체크
                else if (product.getRecommendationCycleDays() != null) {
                    LocalDateTime lastDoneAt = lastDoneMap.get(product.getTaskType().getTaskTypeId());
                    long daysSinceLast = lastDoneAt == null ?
                            9999 : ChronoUnit.DAYS.between(lastDoneAt.toLocalDate(), LocalDate.now());

                    if (daysSinceLast >= product.getRecommendationCycleDays()) {
                        shouldRecommend = true;
                        currentTags.add(SuggestionType.NO_ASSIGNEE);
                        description = product.getDescNoAssignee()
                                .replace("{task_name}", product.getTitle())
                                .replace("{season}", getCurrentSeasonName());
                    }
                }
            }

            if (shouldRecommend) {
                // 시즌이어서 들어왔는데 주기도 지났을 수 있으니 태그 보정
                if (isSeason) {
                    currentTags.add(SuggestionType.GENERAL);
                }

                String fullImgUrl = Optional.ofNullable(s3Properties.getBaseUrl())
                        .map(base -> base + "/" + product.getImgUrl())
                        .orElseThrow(() -> new BusinessException(ErrorCode.S3_CONFIG_ERROR));

                recommendations.add(RecommendationResponse.builder()
                        .suggestionTaskId(product.getSuggestionTaskId())
                        .title(product.getTitle())
                        .imgUrl(fullImgUrl)
                        .defaultEstimatedMinutes(product.getDefaultEstimatedMinutes())
                        .rewardPoint(product.getRewardPoint())
                        .tags(currentTags)
                        .description(description)
                        .build());
            }
        }

        // 우선순위 점수로 정렬하되, 같은 점수 내에서는 섞이도록 하거나 아예 셔플
        List<RecommendationResponse> delayed = new ArrayList<>();
        List<RecommendationResponse> others = new ArrayList<>();

        for (RecommendationResponse r : recommendations) {
            if (r.tags().contains(SuggestionType.DELAYED)) {
                delayed.add(r);
            } else {
                others.add(r);
            }
        }

        // 나머지는 랜덤으로 섞음 (무담당, 시즌이 골고루 섞임)
        java.util.Collections.shuffle(others);

        List<RecommendationResponse> finalResult = new ArrayList<>();
        finalResult.addAll(delayed); // 미룬 일 우선
        finalResult.addAll(others);  // 나머지 랜덤

        return finalResult.stream()
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<EraserTaskOptionsResponse> getTaskOptions(List<Long> suggestionTaskId, Long userId
    ) {
        if (suggestionTaskId == null || suggestionTaskId.isEmpty()) {
            return List.of();
        }

        List<SuggestionTask> tasks = suggestionTaskRepository.findAllById(suggestionTaskId);

        return tasks.stream()
                .map(task -> {
                    // 이미지 URL
                    String fullImgUrl = Optional.ofNullable(s3Properties.getBaseUrl())
                            .map(base -> base + "/" + task.getImgUrl())
                            .orElseThrow(() -> new BusinessException(ErrorCode.S3_CONFIG_ERROR));

                    // 옵션 매핑
                    List<EraserTaskOptionsResponse.OptionDetail> optionDetails = task.getOptions().stream()
                            .map(opt -> new EraserTaskOptionsResponse.OptionDetail(
                                    opt.getOptionId(),
                                    opt.getCount(),
                                    opt.getEstimatedMinutes(),
                                    opt.getPrice()
                            ))
                            .collect(Collectors.toList());

                    return EraserTaskOptionsResponse.builder()
                            .suggestionTaskId(task.getSuggestionTaskId())
                            .title(task.getTitle())
                            .imgUrl(fullImgUrl)
                            .options(optionDetails)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public PaymentInfoResponse getPaymentInfo(Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 유저의 포인트 조회
        int currentPoint = (user.getPointBalance() != null) ? user.getPointBalance() : 0;

        // 사용 가능 포인트 계산(최대 2만 포인트)
        int maxUsablePoint = Math.min(currentPoint, 20000);

        return PaymentInfoResponse.builder()
                .currentPoint(currentPoint)
                .maxUsablePoint(maxUsablePoint)
                .build();
    }

    @Transactional
    public Long completeReservation(ReservationRequest request, Long userId) {
        // 입력값 유효성 검증
        if (request.usedPoint() != null && request.usedPoint() < 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT);
        }
        if (request.reservations() == null || request.reservations().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 유저 조회
        User user = userRepository.findByIdWithPessimisticLock(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 유저의 그룹 ID 조회
        Group group = groupMemberRepository.findGroupByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));
        Long groupId = group.getGroupId();

        // 포인트 잔액 조회
        int usedPoint = (request.usedPoint() != null) ? request.usedPoint() : 0;
        int currentPoint = (user.getPointBalance() != null) ? user.getPointBalance() : 0;

        if (usedPoint > currentPoint) {
            throw new BusinessException(ErrorCode.POINT_NOT_ENOUGH);
        }
        if (usedPoint > MAX_USABLE_POINT) {
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }

        // 예약 아이템 생성 및 총액 계산
        List<ReservationItem> reservationItems = new ArrayList<>();
        int totalPrice = 0;
        int totalRewardPoint = 0;

        List<TaskStatus> targetStatuses = List.of(
                TaskStatus.WAITING,
                TaskStatus.IN_PROGRESS,
                TaskStatus.INCOMPLETED
        );

        for (ReservationRequest.ReservationItemRequest itemReq : request.reservations()) {
            SuggestionTaskOption option = suggestionTaskOptionRepository.findById(itemReq.optionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));

            totalPrice += option.getPrice();

            // 상품별 보상 포인트 합산
            int reward = option.getSuggestionTask().getRewardPoint();
            totalRewardPoint += reward;

            // 청연 지우개 로직 (예약한 상품과 관련된 미완료 집안일 상태 변경)
            Long targetTaskTypeId = option.getSuggestionTask().getTaskType().getTaskTypeId();

            taskOccurrenceRepository.bulkUpdateStatus(
                    groupId,
                    targetTaskTypeId,
                    targetStatuses,
                    TaskStatus.RESOLVED_BY_ERASER
            );

            // 예약 아이템 객체
            ReservationItem item = ReservationItem.builder()
                    .suggestionTaskId(option.getSuggestionTask().getSuggestionTaskId())
                    .taskTitle(option.getSuggestionTask().getTitle())
                    .optionCount(option.getCount())
                    .price(option.getPrice())
                    .visitDate(itemReq.visitDate())
                    .visitTime(itemReq.visitTime())
                    .rewardPoint(reward)
                    .build();

            reservationItems.add(item);
        }

        // 최종 결제 금액 검증
        int finalPrice = totalPrice - usedPoint;
        if (finalPrice < 0) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
        if (usedPoint > 0) {
            user.deductPoint(usedPoint);

            PointTransaction transaction = PointTransaction.builder()
                    .user(user)
                    .amount(-usedPoint)
                    .transactionType(TransactionType.USE_MAGIC_ERASER)
                    .taskLog(null)
                    .build();

            pointTransactionRepository.save(transaction);
        }

        // 포인트 저장
        if (totalRewardPoint > 0) {
            user.addPoint(totalRewardPoint);

            PointTransaction rewardTransaction = PointTransaction.builder()
                    .user(user)
                    .amount(totalRewardPoint)
                    .transactionType(TransactionType.EARN_MAGIC_ERASER)
                    .taskLog(null)
                    .build();

            pointTransactionRepository.save(rewardTransaction);
        }

        // 예약 저장
        Reservation reservation = Reservation.builder()
                .user(user)
                .totalPrice(totalPrice)
                .usedPoint(usedPoint)
                .finalPrice(finalPrice)
                .status(TaskStatus.RESOLVED_BY_ERASER)
                .build();

        for (ReservationItem item : reservationItems) {
            reservation.addReservationItem(item); // 편의 메서드 사용
        }

        Reservation savedReservation = reservationRepository.save(reservation);

        return savedReservation.getReservationId();
    }

    // 특정 그룹, 특정 날짜의 매니저 호출 목록 조회
    public List<ManagerCallResponse> getManagerCalls(Long groupId, LocalDate date) {
        List<ReservationItem> items = reservationItemRepository.findByGroupIdAndDate(groupId, date);

        String fullImgUrl = Optional.ofNullable(s3Properties.getBaseUrl())
                .map(base -> base + MANAGER_PROFILE_IMG)
                .orElseThrow(() -> new BusinessException(ErrorCode.S3_CONFIG_ERROR));

        return items.stream()
                .map(item -> ManagerCallResponse.builder()
                        .reservationItemId(item.getReservationItemId())
                        .imgUrl(fullImgUrl)
                        .serviceName(item.getTaskTitle())
                        .visitTime(item.getVisitTime())
                        .point(item.getRewardPoint())
                        .build())
                .collect(Collectors.toList());
    }

    // 헬퍼 메서드
    private boolean isSeasonMatch(SuggestionTask task) {
        if (task.getSeasonMonths() == null) return false;
        int currentMonth = LocalDate.now().getMonthValue();
        String[] months = task.getSeasonMonths().split(",");
        for (String m : months) {
            if (Integer.parseInt(m.trim()) == currentMonth) return true;
        }
        return false;
    }

    private String getCurrentSeasonName() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 3 && month <= 5) return "봄";
        if (month >= 6 && month <= 8) return "여름";
        if (month >= 9 && month <= 11) return "가을";
        return "겨울";
    }

    private int getPriorityScore(List<SuggestionType> tags) {
        if (tags.contains(SuggestionType.DELAYED)) return 1;      // 1순위: 미룬 것
        if (tags.contains(SuggestionType.NO_ASSIGNEE)) return 2;  // 2순위: 담당자 없는 것
        return 3;                                                 // 3순위: 시즌 추천 등
    }
}