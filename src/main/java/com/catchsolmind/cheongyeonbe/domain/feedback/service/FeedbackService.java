package com.catchsolmind.cheongyeonbe.domain.feedback.service;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.FeedbackCreateRequest;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.FeedbackResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.GroupMemberWithTestResult;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.PraiseTypeResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.ReportResponse;
import com.catchsolmind.cheongyeonbe.domain.feedback.entity.Feedback;
import com.catchsolmind.cheongyeonbe.domain.feedback.entity.ImprovementFeedback;
import com.catchsolmind.cheongyeonbe.domain.feedback.repository.FeedbackRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.entity.HouseworkTestResult;
import com.catchsolmind.cheongyeonbe.domain.houseworktest.repository.HouseworkTestResultRepository;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FeedbackService {
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final HouseworkTestResultRepository houseworkTestResultRepository;
    private final FeedbackRepository feedbackRepository;

    public FeedbackResponse getFeedback(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GroupMember currentMember = groupMemberRepository.findByUser_UserIdAndStatus(userId, MemberStatus.AGREED)
                .orElseThrow(() -> new BusinessException(ErrorCode.NEED_AGREEMENT_APPROVAL));

        Long groupId = currentMember.getGroup().getGroupId();
        List<GroupMember> groupMembers = groupMemberRepository.findByGroup_GroupIdAndStatus(groupId, MemberStatus.AGREED);

        List<Long> userIds = groupMembers.stream()
                .map(gm -> gm.getUser().getUserId())
                .toList();

        Map<Long, TestResultType> testResultMap = houseworkTestResultRepository.findByUser_UserIdIn(userIds).stream()
                .collect(Collectors.toMap(
                        result -> result.getUser().getUserId(),
                        HouseworkTestResult::getResultType,
                        (existing, replacement) -> existing
                ));

        List<GroupMemberWithTestResult> memberDtos = groupMembers.stream()
                .filter(gm -> !gm.getUser().getUserId().equals(userId)) // 본인 제외
                .map(gm -> {
                    Long memberUserId = gm.getUser().getUserId();
                    return GroupMemberWithTestResult.builder()
                            .groupMemberId(gm.getGroupMemberId())
                            .nickname(gm.getUser().getNickname())
                            .profileImageUrl(gm.getUser().getProfileImg())
                            .testResultType(testResultMap.get(memberUserId))
                            .build();
                })
                .toList();

        List<PraiseTypeResponse> praiseTypes = Arrays.stream(PraiseType.values())
                .map(PraiseTypeResponse::from)
                .toList();
        List<TaskCategory> taskCategories = Arrays.asList(TaskCategory.values());

        return new FeedbackResponse(memberDtos, praiseTypes, taskCategories);
    }

    @Transactional
    public void postFeedback(Long userId, FeedbackCreateRequest request) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // Author 조회 및 검증
        GroupMember author = groupMemberRepository.findByUser_UserIdAndStatus(userId, MemberStatus.AGREED)
                .orElseThrow(() -> new BusinessException(ErrorCode.NEED_AGREEMENT_APPROVAL));

        // Target 조회
        GroupMember target = groupMemberRepository.findById(request.targetMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        // 유효성 검증
        // 본인에게 쓰는지 확인
        if (author.getGroupMemberId().equals(target.getGroupMemberId())) {
            throw new BusinessException(ErrorCode.CANNOT_FEEDBACK_SELF);
        }
        // 같은 그룹인지 확인
        if (!author.getGroup().getGroupId().equals(target.getGroup().getGroupId())) {
            throw new BusinessException(ErrorCode.NOT_SAME_GROUP);
        }

        // ImprovementFeedback 엔티티 변환
        List<ImprovementFeedback> improvementEntities = new ArrayList<>();
        if (request.improvements() != null && !request.improvements().isEmpty()) {
            improvementEntities = request.improvements().stream()
                    .map(dto -> ImprovementFeedback.builder()
                            .category(dto.category())
                            .rawText(dto.content())
                            .aiStatus(AiStatus.UNCOMPLETED) // 초기값 설정
                            .build())
                    .toList();
        }

        // Feedback 엔티티 생성 및 매핑
        Feedback feedback = Feedback.builder()
                .group(author.getGroup())
                .author(author)
                .target(target)
                .praiseTypes(request.praiseTypes())
                .improvements(new ArrayList<>(improvementEntities))
                .build();

        // 저장
        feedbackRepository.save(feedback);

        log.info("[피드백] 작성자: {}, 대상자: {}, 칭찬: {}, 개선사항수: {}",
                author.getGroupMemberId(), target.getGroupMemberId(), request.praiseTypes().size(), improvementEntities.size());
    }

    public ReportResponse getReport(Long userId) {
        // 유저 및 그룹 조회
        GroupMember currentMember = groupMemberRepository.findByUser_UserIdAndStatus(userId, MemberStatus.AGREED)
                .orElseThrow(() -> new BusinessException(ErrorCode.NEED_AGREEMENT_APPROVAL));
        Long groupId = currentMember.getGroup().getGroupId();

        // 날짜 계산 (이번 주 월요일 00:00 ~ 오늘/일요일 23:59)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        // 데이터 조회
        List<Feedback> weeklyFeedbacks = feedbackRepository.findAllByGroupIdAndDateRange(groupId, startOfWeek, endOfWeek);

        // 통계 계산: 1위 칭찬, 1위 개선점, 주말 여부
        PraiseType topPraise = getTopPraise(weeklyFeedbacks);
        TaskCategory topCategory = getTopCategory(weeklyFeedbacks);
        boolean isWeekendPattern = checkWeekendPattern(weeklyFeedbacks);

        // 타이틀 및 3줄 요약 생성
        String title = generateReportTitle(topPraise);
        List<String> summaries = generateSummaries(topPraise, topCategory, isWeekendPattern);

        // 내가 받은 피드백
        List<Feedback> myReceivedFeedbacks = weeklyFeedbacks.stream()
                .filter(f -> f.getTarget().getUser().getUserId().equals(userId))
                .toList();

        // 내가 받은 칭찬 스탬프
        List<PraiseTypeResponse> myPraises = myReceivedFeedbacks.stream()
                .flatMap(f -> f.getPraiseTypes().stream())
                .map(PraiseTypeResponse::from)
                .toList();

        // 내 개선 피드백
        List<ReportResponse.MyImprovementResponse> myImprovements = myReceivedFeedbacks.stream()
                .flatMap(f -> f.getImprovements().stream()
                        .map(improvement -> ReportResponse.MyImprovementResponse.builder()
                                .category(improvement.getCategory())
                                .content(improvement.getRawText())
                                .authorName(f.getAuthor().getUser().getNickname())
                                .profileImageUrl(f.getAuthor().getUser().getProfileImg())
                                .build()))
                .toList();

        // 멤버 피드백
        List<ReportResponse.MemberFeedbackPreviewResponse> memberPreviews = getMemberPreviews(groupId, userId, weeklyFeedbacks);

        return ReportResponse.builder()
                .period(String.format("%d년 %d월 %d주차", now.getYear(), now.getMonthValue(), getWeekOfMonth(now)))
                .groupTitle(title)
                .summaries(summaries)
                .myPraiseStamp(myPraises)
                .myImprovements(myImprovements)
                .memberFeedbacks(memberPreviews)
                .build();
    }

    // --- 헬퍼 메서드 ---

    // 리포트 타이틀 생성
    private String generateReportTitle(PraiseType topPraise) {
        if (topPraise == null) return "평화로운 우리집"; // default

        return switch (topPraise) {
            case DETAIL_KING -> "완벽주의 팀플러";
            case TIME_KEEPER -> "칼 같은 시간 약속러";
            case DUST_KILLER -> "먼지 사냥꾼들";
            case ORGANIZING_KING -> "정리의 신";
            case SCENT_KING -> "향기로운 무드메이커";
            case POINT_KING -> "성실한 개미 군단";
        };
    }

    // 3줄 요약 생성
    private List<String> generateSummaries(PraiseType topPraise, TaskCategory topCategory, boolean isWeekendPattern) {
        List<String> lines = new ArrayList<>();

        // 긍정: 1위 칭찬 관련
        if (topPraise != null) {
            lines.add("이번 주는 '" + topPraise.getDescription() + "' 칭찬이 가장 많았어요!");
        } else {
            lines.add("아직 주고받은 피드백이 없어요. 먼저 시작해보세요!");
        }

        // 개선: 가장 많이 지적된 카테고리 관련
        if (topCategory != null) {
            String categoryMsg = switch (topCategory) {
                case BATHROOM -> "특히 '화장실' 청소에 신경 쓰면 더 좋겠어요.";
                case KITCHEN -> "'설거지'와 '음식물 처리'가 주요 이슈였어요.";
                case LAUNDRY -> "빨래가 밀리지 않도록 '세탁'과 '정리'를 챙겨주세요.";
                case BEDROOM -> "'침구 정리'와 '환기'로 쾌적한 침실을 만들어봐요.";
                case LIVING -> "'거실' 정돈에 집중하는 한 주였어요.";
                case TRASH -> "'분리수거'와 '쓰레기 처리'가 중요했어요.";
                case ETC -> "반려동물 케어나 육아 등 '기타 집안일' 분담이 필요해요.";
                default -> "집안일 분담에 조금 더 신경 써봐요.";
            };
            lines.add(categoryMsg);
        } else {
            lines.add("지적받은 개선 사항 없이 완벽했어요! \uD83D\uDC4D");
        }

        // 패턴: 시간대/요일 통계
        if (isWeekendPattern) {
            lines.add("주말 전 미리 청소하는 습관이 돋보여요!");
        } else {
            lines.add("평일에도 틈틈이 관리하는 성실함이 보여요!");
        }

        return lines;
    }

    // 가장 많이 받은 칭찬 스티커 1개 찾기
    private PraiseType getTopPraise(List<Feedback> feedbacks) {
        return feedbacks.stream()
                .flatMap(f -> f.getPraiseTypes().stream())
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // 가장 많이 언급된 개선 카테고리 1개 찾기
    private TaskCategory getTopCategory(List<Feedback> feedbacks) {
        return feedbacks.stream()
                .flatMap(f -> f.getImprovements().stream())
                .collect(Collectors.groupingBy(ImprovementFeedback::getCategory, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // 피드백이 주말에 집중되었는지 확인 (과반수 이상이면 true)
    private boolean checkWeekendPattern(List<Feedback> feedbacks) {
        if (feedbacks.isEmpty()) return false;

        long weekendCount = feedbacks.stream()
                .filter(f -> {
                    DayOfWeek day = f.getCreatedAt().getDayOfWeek();
                    return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
                })
                .count();
        return weekendCount > (feedbacks.size() / 2.0);
    }

    // 주차 계산 (단순화)
    private int getWeekOfMonth(LocalDateTime date) {
        return (date.getDayOfMonth() - 1) / 7 + 1; // TODO: WeekFields로 리팩토링
    }

    // 멤버별 최신 피드백 미리보기 생성
    private List<ReportResponse.MemberFeedbackPreviewResponse> getMemberPreviews(Long groupId, Long myUserId, List<Feedback> weeklyFeedbacks) {
        // 그룹의 다른 멤버들 조회 (AGREED 상태만)
        List<GroupMember> otherMembers = groupMemberRepository.findByGroup_GroupIdAndStatus(groupId, MemberStatus.AGREED).stream()
                .filter(m -> !m.getUser().getUserId().equals(myUserId)) // 나는 제외
                .toList();

        // 각 멤버별로 이번 주에 받은 피드백 중 가장 최신 1개 추출
        return otherMembers.stream().map(member -> {
            // 이 멤버가 받은 피드백만 필터링 -> 최신순 정렬
            Optional<Feedback> latestFeedback = weeklyFeedbacks.stream()
                    .filter(f -> f.getTarget().getGroupMemberId().equals(member.getGroupMemberId()))
                    .max((f1, f2) -> f1.getCreatedAt().compareTo(f2.getCreatedAt())); // 가장 최근 작성된 것

            // 피드백 내용 추출 (개선사항이 있으면 그거, 없으면 칭찬 받음)
            String content = latestFeedback
                    .flatMap(f -> f.getImprovements().stream().findFirst().map(ImprovementFeedback::getRawText))
                    .orElseGet(() -> latestFeedback.isPresent() ? "칭찬 스티커를 받았어요!" : "아직 받은 피드백이 없어요");

            return ReportResponse.MemberFeedbackPreviewResponse.builder()
                    .memberId(member.getGroupMemberId())
                    .nickname(member.getUser().getNickname())
                    .latestFeedbackContent(content)
                    .build();
        }).toList();
    }
}
