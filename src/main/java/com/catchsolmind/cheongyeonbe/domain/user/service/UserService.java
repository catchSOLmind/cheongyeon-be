package com.catchsolmind.cheongyeonbe.domain.user.service;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;

public interface UserService {
    UserDto findOrCreate(OAuthUserInfo info);

    User findEntityById(Long userId);
}
