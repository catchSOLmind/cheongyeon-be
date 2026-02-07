package com.catchsolmind.cheongyeonbe.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통
    INVALID_INPUT_VALUE("COMMON001", "요청 값 검증"),

    // User
    USER_NOT_FOUND("USER001", "사용자를 찾을 수 없습니다."),
    INVALID_HOUSEWORK_TYPE("USER002", "유효하지 않은 가사 성향 타입입니다."),

    // 포인트
    POINT_NOT_ENOUGH("POINT001", "포인트가 부족합니다."),
    INVALID_POINT_AMOUNT("POINT002", "최대 사용 포인트보다 많습니다."),
    INVALID_POINT("POINT003", "잘못된 포인트 (음수 포인트 불가)"),

    // 예약
    OPTION_NOT_FOUND("RESERVATION003", "옵션이 없습니다."),
    INVALID_PAYMENT_AMOUNT("RESERVATION004", "유효하지 않은 결제 금액입니다."),

    // Group
    GROUP_NOT_FOUND("GROUP001", "그룹을 찾을 수 없습니다."),
    NOT_SAME_GROUP("GROUP002", "같은 그룹이 아닙니다."),

    // Member
    MEMBER_NOT_FOUND("MEMBER001", "멤버를 찾을 수 없습니다."),

    // File
    INVALID_FILE_TYPE("FILE001", "허용되지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED("FILE002", "파일 크기가 제한을 초과했습니다."),
    FILE_UPLOAD_FAILED("FILE003", "파일 업로드에 실패했습니다."),
    S3_CONFIG_ERROR("FILE004", "s3 관련 정보를 확인하세요."),

    // OAuth
    KAKAO_SERVER_ERROR("OAUTH001", "카카오 서버 오류가 발생했습니다."),
    EXPIRED_TOKEN("AUTH002", "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN("AUTH003", "지원하지 않는 토큰입니다."),
    AUTHENTICATION_FAILED("AUTH004", "인증에 실패했습니다."),
    INVALID_TOKEN("AUTH005", "유효하지 않은 토큰입니다."),
    UNAUTHORIZED_USER("AUTH006", "인증되지 않은 사용자입니다."),

    // Housework Test
    QUESTION_NOT_FOUND("TEST001", "가사 성향 테스트 질문이 존재하지 않습니다."),
    CHOICE_NOT_FOUND("TEST002", "질문에 대한 선택지가 존재하지 않습니다."),
    INVALID_CHOICE("TEST003", "유효하지 않은 선택입니다."),

    // 협약서
    NEED_AGREEMENT_APPROVAL("AGREEMENT001", "협약서 동의가 필요합니다."),

    // 피드백
    CANNOT_FEEDBACK_SELF("FEEDBACK001", "자기자신에게 피드백을 줄 수 없습니다.");

    private final String code;
    private final String message;
}
