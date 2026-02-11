package com.catchsolmind.cheongyeonbe.domain.user.service.basic;

import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupRepository;
import com.catchsolmind.cheongyeonbe.domain.auth.dto.data.OAuthUserInfo;
import com.catchsolmind.cheongyeonbe.domain.user.dto.UserDto;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.mapper.UserMapper;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.domain.user.service.UserService;
import com.catchsolmind.cheongyeonbe.global.BusinessException;
import com.catchsolmind.cheongyeonbe.global.ErrorCode;
import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
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

        createSoloGroup(savedUser);

        return userMapper.toDto(savedUser);
    }

    private void createSoloGroup(User user) {
        Group newGroup = Group.builder()
                .name(user.getNickname() + "의 우리 집")
                .ownerUser(user)
                .build();

        Group savedGroup = groupRepository.save(newGroup);

        GroupMember newMember = GroupMember.builder()
                .group(savedGroup)
                .user(user)
                .role(MemberRole.OWNER)
                .status(MemberStatus.JOINED)
                .build();

        groupMemberRepository.save(newMember);
    }
}
