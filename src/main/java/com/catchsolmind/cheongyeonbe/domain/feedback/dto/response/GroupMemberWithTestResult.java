package com.catchsolmind.cheongyeonbe.domain.feedback.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TestResultType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record GroupMemberWithTestResult(
        @Schema(description = "그룹 멤버 ID (피드백 대상 ID)")
        Long groupMemberId,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "프로필 이미지 URL")
        String profileImageUrl,

        @Schema(description = "가사 성향 타입 (테스트 안했으면 null)")
        TestResultType testResultType
) {
}
