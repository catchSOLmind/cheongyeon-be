package com.catchsolmind.cheongyeonbe.domain.user.service.basic;

import com.catchsolmind.cheongyeonbe.domain.oauth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.mapper.UserMapper;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.user.UserDtoFixture;
import com.catchsolmind.cheongyeonbe.global.fixture.entity.UserFixture;
import com.catchsolmind.cheongyeonbe.global.fixture.dto.oauth.OAuthUserInfoFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasicUserServiceTest {
    @InjectMocks
    BasicUserService userService;

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("이미 가입된 카카오 유저라면 기존 유저를 반환한다")
    void findExistingKakaoUser() {
        // given
        OAuthUserInfo info = OAuthUserInfoFixture.kakaoUser();

        User existingUser = UserFixture.base();
        UserDto expectedDto = UserDtoFixture.valid();

        when(userRepository.findByProviderAndProviderId(AuthProvider.KAKAO, info.providerId()))
                .thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(existingUser))
                .thenReturn(expectedDto);

        // when
        UserDto result = userService.findOrCreate(info);

        // then
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("처음 로그인한 카카오 유저라면 회원가입 후 반환한다")
    void createNewKakaoUser() {
        // given
        OAuthUserInfo info = OAuthUserInfoFixture.kakaoUser();

        when(userRepository.findByProviderAndProviderId(AuthProvider.KAKAO, info.providerId()))
                .thenReturn(Optional.empty());

        User savedUser = UserFixture.base();
        UserDto expectedDto = UserDtoFixture.valid();

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);
        when(userMapper.toDto(savedUser))
                .thenReturn(expectedDto);
        // when
        UserDto result = userService.findOrCreate(info);

        // then
        assertThat(result).isEqualTo(expectedDto);
    }
}