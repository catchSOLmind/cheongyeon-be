package com.catchsolmind.cheongyeonbe.domain.feedback.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record FeedbackResponse(
        @Schema(description = "그룹 멤버 List")
        List<GroupMemberWithTestResult> groupMembers,

        @Schema(description = "칭찬 스탬프")
        List<PraiseTypeResponse> praiseTypes,

        @Schema(description = "카테고리")
        List<TaskCategory> taskCategories
) {
}
