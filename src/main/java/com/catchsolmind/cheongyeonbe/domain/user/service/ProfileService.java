package com.catchsolmind.cheongyeonbe.domain.user.service;

import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.entity.MemberPreference;
import com.catchsolmind.cheongyeonbe.domain.user.dto.*;
import com.catchsolmind.cheongyeonbe.domain.user.dto.request.ProfileUpdateRequest;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileGetResponse;
import com.catchsolmind.cheongyeonbe.domain.user.dto.response.ProfileUpdateResponse;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.MemberPreferenceRepository;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final MemberPreferenceRepository memberPreferenceRepository;

    public ProfileGetResponse getProfile(User user, GroupMember member) {

        String type = null;

        // 로그인 연결 전 테스트 로직
        if (member != null && member.getGroupMemberId() != null) {
            MemberPreference pref = memberPreferenceRepository.findByMember(member).orElse(null);
            type = (pref != null) ? pref.getPersonalityType() : null;
        }

        return ProfileGetResponse.builder()
                .profile(ProfileGetResponse.Profile.builder()
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .profileImageUrl(user.getProfileImg())
                        .houseworkType(type)
                        .houseworkTypeLabel(HouseworkTypeMapper.labelOf(type))
                        .build())
                .build();
    }

    public ProfileUpdateResponse updateProfile(User user, GroupMember member, ProfileUpdateRequest request
    ) {
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }

        userRepository.save(user);

        // 성향 테스트는 member 가 존재할떄 수행
        if (request.getHouseworkType() != null) {
            if (member == null || member.getGroupMemberId() == null) {
            } else {
                MemberPreference pref = memberPreferenceRepository.findByMember(member)
                        .orElseGet(() -> memberPreferenceRepository.save(
                                MemberPreference.builder()
                                        .member(member)
                                        .build()
                        ));

                pref.setPersonalityType(request.getHouseworkType());
                memberPreferenceRepository.save(pref);
            }
        }

        return ProfileUpdateResponse.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .houseworkType(request.getHouseworkType())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
