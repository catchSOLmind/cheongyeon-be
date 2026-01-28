package com.catchsolmind.cheongyeonbe.domain.user.service;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.response.KakaoUserResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;

public interface UserService {
    UserDto findOrCreateUser(KakaoUserResponse kakaoUser);
}
