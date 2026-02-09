package com.catchsolmind.cheongyeonbe.domain.feedback.dto.response;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.OpenAiRequest;

import java.util.List;

// --- OpenAI API로부터 받은 응답 데이터 ---

public record OpenAiResponse(
        List<Choice> choices // AI가 생성한 답변 후보 리스트
) {
    public record Choice(OpenAiRequest.Message message) { // message AI가 생성한 메시지 내용
    }
}
