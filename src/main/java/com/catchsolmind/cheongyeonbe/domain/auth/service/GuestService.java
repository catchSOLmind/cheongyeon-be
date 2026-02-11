package com.catchsolmind.cheongyeonbe.domain.auth.service;

import com.catchsolmind.cheongyeonbe.domain.agreement.entity.Agreement;
import com.catchsolmind.cheongyeonbe.domain.agreement.entity.AgreementItem;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementItemRepository;
import com.catchsolmind.cheongyeonbe.domain.agreement.repository.AgreementRepository;
import com.catchsolmind.cheongyeonbe.domain.auth.dto.response.GuestLoginResponse;
import com.catchsolmind.cheongyeonbe.domain.auth.entity.RefreshToken;
import com.catchsolmind.cheongyeonbe.domain.auth.repository.RefreshTokenRepository;
import com.catchsolmind.cheongyeonbe.domain.group.entity.Group;
import com.catchsolmind.cheongyeonbe.domain.group.entity.GroupMember;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupMemberRepository;
import com.catchsolmind.cheongyeonbe.domain.group.repository.GroupRepository;
import com.catchsolmind.cheongyeonbe.domain.task.entity.Task;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskOccurrence;
import com.catchsolmind.cheongyeonbe.domain.task.entity.TaskType;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskRepository;
import com.catchsolmind.cheongyeonbe.domain.task.repository.TaskTypeRepository;
import com.catchsolmind.cheongyeonbe.domain.user.entity.User;
import com.catchsolmind.cheongyeonbe.domain.user.repository.UserRepository;
import com.catchsolmind.cheongyeonbe.global.enums.AuthProvider;
import com.catchsolmind.cheongyeonbe.global.enums.MemberRole;
import com.catchsolmind.cheongyeonbe.global.enums.MemberStatus;
import com.catchsolmind.cheongyeonbe.global.enums.TaskStatus;
import com.catchsolmind.cheongyeonbe.global.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final TaskRepository taskRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final AgreementRepository agreementRepository;
    private final AgreementItemRepository agreementItemRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public GuestLoginResponse enterGuestMode() {

        User dummyOwner = createTempUser("하우스_매니저", "https://api.dicebear.com/7.x/adventurer/svg?seed=Felix");

        User dummyMate = createTempUser("청소_요정", "https://api.dicebear.com/7.x/adventurer/svg?seed=Coco");

        String guestNickname = "게스트_" + UUID.randomUUID().toString().substring(0, 4);
        User guestUser = createTempUser(guestNickname, "https://api.dicebear.com/7.x/adventurer/svg?seed=" + guestNickname);

        Group group = Group.builder()
                .name(guestNickname + "님의 쉐어하우스")
                .ownerUser(dummyOwner)
                .build();
        groupRepository.save(group);

        joinMember(group, dummyOwner, MemberRole.OWNER, MemberStatus.AGREED);

        joinMember(group, dummyMate, MemberRole.MEMBER, MemberStatus.AGREED);

        GroupMember guestMember = joinMember(group, guestUser, MemberRole.MEMBER, MemberStatus.JOINED);

        createDummyAgreement(group);

        createDummyTask(group, guestMember, 26L, "설거지", "저녁 먹은 그릇 설거지하기", true);
        createDummyTask(group, guestMember, 95L, "쓰레기 버리기", "종량제 봉투 채워서 버리기", true);

        String accessToken = jwtProvider.createAccessToken(guestUser.getUserId());
        String refreshTokenVal = jwtProvider.createRefreshToken(guestUser.getUserId());

        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshTokenVal)
                .user(guestUser)
                .expiresAt(LocalDateTime.now().plusDays(14))
                .build());

        return GuestLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenVal)
                .userId(guestUser.getUserId())
                .nickname(guestUser.getNickname())
                .groupId(group.getGroupId())
                .groupName(group.getName())
                .memberStatus(guestMember.getStatus().name()) // "JOINED" 확인용
                .build();
    }

    // --- Helper Methods ---

    private User createTempUser(String nickname, String profileImg) {
        Long randomProviderId = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
        User user = User.builder()
                .nickname(nickname)
                .provider(AuthProvider.GUEST) // ★ DB ENUM 추가 필수
                .providerId(randomProviderId)
                .pointBalance(0)
                .profileImg(profileImg) // 프로필 이미지
                .build();
        return userRepository.save(user);
    }

    private GroupMember joinMember(Group group, User user, MemberRole role, MemberStatus status) {
        GroupMember member = GroupMember.builder()
                .user(user)
                .group(group)
                .role(role)
                .status(status)
                .build();
        return groupMemberRepository.save(member);
    }

    private void createDummyAgreement(Group group) {
        Agreement agreement = Agreement.builder()
                .group(group)
                .title("우리 집 생활 수칙")
                .houseName(group.getName())
                .monthlyGoal("배려하며 지내기")
                .deadline(LocalDate.now().plusMonths(1))
                .status(com.catchsolmind.cheongyeonbe.global.enums.AgreementStatus.CONFIRMED)
                .build();
        agreementRepository.save(agreement);

        List<String> rules = List.of("사용한 물건 제자리", "밤 12시 소등", "설거지는 바로바로");
        for (int i = 0; i < rules.size(); i++) {
            agreementItemRepository.save(AgreementItem.builder()
                    .agreement(agreement)
                    .itemOrder(i + 1)
                    .itemText(rules.get(i))
                    .build());
        }
    }

    private void createDummyTask(Group group, GroupMember member, Long taskTypeId, String title, String desc, boolean createOccurrenceToday) {
        TaskType taskType = taskTypeRepository.findById(taskTypeId).orElse(null);
        if (taskType == null) return;

        Task task = Task.builder()
                .group(group)
                .taskType(taskType)
                .title(title)
                .description(desc)
                .creatorMember(member)
                .status(TaskStatus.WAITING)
                .time("10:00")
                .build();

        if (createOccurrenceToday) {
            TaskOccurrence occurrence = TaskOccurrence.builder()
                    .task(task)
                    .group(group)
                    .occurDate(LocalDate.now())
                    .primaryAssignedMember(member)
                    .status(TaskStatus.WAITING)
                    .build();
            task.getOccurrences().add(occurrence);
        }
        taskRepository.save(task);
    }
}