package com.catchsolmind.cheongyeonbe.domain.feedback.dto.request;

import java.util.List;

// --- OpenAI API에 보낼 요청 데이터 ---

public record OpenAiRequest(
        String model, // 사용할 AI 모델명

        List<Message> messages // 대화 내용 리스트 (시스템 지시 사항 + 사용자 질문)
) {
    public record Message(
            String role, // 말하는 사람의 역할 (system, user, assistant)

            String content // 말하는 내용
    ) {
    }
}
