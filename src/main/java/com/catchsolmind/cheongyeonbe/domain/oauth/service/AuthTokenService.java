package com.catchsolmind.cheongyeonbe.domain.oauth.service;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.request.RefreshTokenRequest;
import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.RefreshTokenResponse;

public interface AuthTokenService {
    RefreshTokenResponse refresh(RefreshTokenRequest request);
}
