package com.catchsolmind.cheongyeonbe.domain.group.service;

import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupInvitationAcceptResponse;
import com.catchsolmind.cheongyeonbe.domain.group.dto.response.GroupInvitationCreateResponse;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupInvitation;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupInvitationRepository;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInvitationRepository groupInvitationRepository;

    @Value("${invitation.base-url}")
    private String baseUrl;

    /**
     * 그룹 생성
     * - 기존 임시 그룹에서 탈퇴(LEFT) 처리
     * - 새 그룹 생성 후 OWNER로 등록
     */
    public GroupCreateResponse createGroup(User user) {
        // 1. 기존 그룹 멤버십 탈퇴 처리
        leaveCurrentGroup(user);

        // 2. 새 그룹 생성
        Group newGroup = Group.builder()
                .name("새 그룹")
                .ownerUser(user)
                .build();
        groupRepository.save(newGroup);

        // 3. OWNER로 그룹 멤버 등록
        GroupMember ownerMember = GroupMember.builder()
                .group(newGroup)
                .user(user)
                .role(MemberRole.OWNER)
                .status(MemberStatus.JOINED)
                .build();
        groupMemberRepository.save(ownerMember);

        return GroupCreateResponse.builder()
                .groupId(newGroup.getGroupId())
                .role(MemberRole.OWNER)
                .createdAt(newGroup.getCreatedAt())
                .build();
    }

    /**
     * 초대 링크 생성
     * - OWNER만 생성 가능
     */
    public GroupInvitationCreateResponse createInvitation(GroupMember member) {
        // OWNER 권한 확인
        if (member.getRole() != MemberRole.OWNER) {
            throw new IllegalArgumentException("초대 링크는 그룹 대표자만 생성할 수 있습니다.");
        }

        GroupInvitation invitation = GroupInvitation.builder()
                .group(member.getGroup())
                .createdBy(member)
                .build();
        groupInvitationRepository.save(invitation);

        String inviteUrl = baseUrl + "/invite/" + invitation.getInvitationId();

        return GroupInvitationCreateResponse.builder()
                .invitationId(invitation.getInvitationId())
                .inviteUrl(inviteUrl)
                .build();
    }

    /**
     * 초대 수락 (그룹 가입)
     * - 기존 임시 그룹에서 탈퇴(LEFT) 처리
     * - 새 그룹에 MEMBER로 등록
     * - 초대 링크는 재사용 가능
     */
    public GroupInvitationAcceptResponse acceptInvitation(User user, Long invitationId) {
        // 1. 초대 유효성 확인 (재사용 가능 - usedAt 체크 없음)
        GroupInvitation invitation = groupInvitationRepository.findByInvitationId(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대입니다."));

        Group group = invitation.getGroup();

        // 2. 이미 그룹 멤버인지 확인 (JOINED 또는 AGREED 상태)
        boolean alreadyMember = groupMemberRepository
                .findByGroup_GroupIdAndUser_UserIdAndStatusNot(group.getGroupId(), user.getUserId(), MemberStatus.LEFT)
                .isPresent();
        if (alreadyMember) {
            throw new IllegalArgumentException("이미 해당 그룹의 멤버입니다.");
        }

        // 3. 기존 그룹 멤버십 탈퇴 처리
        leaveCurrentGroup(user);

        // 4. 새 그룹에 MEMBER로 등록
        GroupMember newMember = GroupMember.builder()
                .group(group)
                .user(user)
                .role(MemberRole.MEMBER)
                .status(MemberStatus.JOINED)
                .build();
        groupMemberRepository.save(newMember);

        // 5. 초대 사용 기록 (재사용 가능하므로 마지막 사용자 정보만 업데이트)
        invitation.setUsedAt(LocalDateTime.now());
        invitation.setUsedBy(user);
        groupInvitationRepository.save(invitation);

        return GroupInvitationAcceptResponse.builder()
                .groupId(group.getGroupId())
                .memberId(newMember.getGroupMemberId())
                .role(MemberRole.MEMBER)
                .status(MemberStatus.JOINED)
                .joinedAt(newMember.getJoinedAt())
                .build();
    }

    /**
     * 현재 그룹에서 탈퇴 처리
     */
    private void leaveCurrentGroup(User user) {
        groupMemberRepository.findFirstByUser_UserIdOrderByGroupMemberIdDesc(user.getUserId())
                .ifPresent(member -> {
                    member.setStatus(MemberStatus.LEFT);
                    groupMemberRepository.save(member);
                });
    }
}
