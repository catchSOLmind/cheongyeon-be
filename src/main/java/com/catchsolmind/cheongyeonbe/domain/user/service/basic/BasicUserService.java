package com.catchsolmind.cheongyeonbe.domain.user.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.mapper.UserMapper;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.domain.user.service.UserService;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto findOrCreate(OAuthUserInfo info) {

        return userRepository
                .findByProviderAndProviderId(info.provider(), info.providerId())
                .map(userMapper::toDto)
                .orElseGet(() -> createUser(info));
    }

    @Override
    public User findEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private UserDto createUser(OAuthUserInfo info) {
        User user = User.createOAuthUser(info);
        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }
}
