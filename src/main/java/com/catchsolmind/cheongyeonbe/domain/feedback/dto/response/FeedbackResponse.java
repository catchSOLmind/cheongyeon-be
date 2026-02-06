package com.catchsolmind.cheongyeonbe.domain.feedback.dto.response;

import com.catchsolmind.cheongyeonbe.global.enums.PraiseType;
import com.catchsolmind.cheongyeonbe.global.enums.TaskCategory;

import java.util.List;

public record FeedbackResponse(
        List<GroupMemberWithTestResponse> groupMembers,

        List<PraiseType> praiseTypes,

        List<TaskCategory> taskCategories
) {
}
