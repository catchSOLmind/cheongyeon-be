package com.catchsolmind.cheongyeonbe.domain.auth.service;

import com.catchsolmind.cheongyeonbe.domain.auth.dto.request.RefreshTokenRequest;
import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.RefreshTokenResponse;

public interface AuthTokenService {
    RefreshTokenResponse refresh(RefreshTokenRequest request);
}
