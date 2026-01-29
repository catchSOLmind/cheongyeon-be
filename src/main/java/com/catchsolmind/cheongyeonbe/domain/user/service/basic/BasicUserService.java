package com.catchsolmind.cheongyeonbe.domain.user.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.mapper.UserMapper;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.domain.user.service.UserService;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findOrCreate(OAuthUserInfo info) {
        return userRepository
                .findByProviderAndProviderId(AuthProvider.KAKAO, info.providerId())
                .map(userMapper::toDto)
                .orElseGet(() -> createUser(info));
    }

    private UserDto createUser(OAuthUserInfo info) {
        User user = User.createOAuthUser(info);

        return userMapper.toDto(userRepository.save(user));
    }
}
