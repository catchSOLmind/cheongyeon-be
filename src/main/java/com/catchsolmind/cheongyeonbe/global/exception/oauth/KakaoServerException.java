package com.catchsolmind.cheongyeonbe.global.exception.oauth;

import com.catchsolmind.cheongyeonbe.global.exception.BusinessException;
import com.catchsolmind.cheongyeonbe.global.exception.ErrorCode;

public class KakaoServerException extends BusinessException {
    public KakaoServerException() {
        super(ErrorCode.KAKAO_SERVER_ERROR);
    }
}
