package com.catchsolmind.cheongyeonbe.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User
    USER_NOT_FOUND("USER001", "사용자를 찾을 수 없습니다."),
    INVALID_HOUSEWORK_TYPE("USER002", "유효하지 않은 가사 성향 타입입니다."),

    // File
    INVALID_FILE_TYPE("FILE001", "허용되지 않는 파일 형식입니다."),
    FILE_SIZE_EXCEEDED("FILE002", "파일 크기가 제한을 초과했습니다."),
    FILE_UPLOAD_FAILED("FILE003", "파일 업로드에 실패했습니다.");

    private final String code;
    private final String message;
}
