package com.catchsolmind.cheongyeonbe.domain.feedback.service;

import com.catchsolmind.cheongyeonbe.domain.feedback.dto.request.OpenAiRequest;
import com.catchsolmind.cheongyeonbe.domain.feedback.dto.response.OpenAiResponse;
import com.catchsolmind.cheongyeonbe.global.properties.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FeedbackAiService {

    private final OpenAiProperties openAiProperties;
    private final RestTemplate restTemplate;

    // 병렬로 동시에 변환
    @Transactional
    public List<String> refineBatch(List<String> rawContents) {
        if (rawContents == null || rawContents.isEmpty()) {
            return List.of();
        }

        // 각 문장에 대해 비동기 요청 생성 (Future 리스트 만들기)
        List<CompletableFuture<String>> futures = rawContents.stream()
                .map(content -> CompletableFuture.supplyAsync(() -> getRefinedText(content)))
                .toList();

        // 모든 요청이 끝날 때까지 기다렸다가 결과 수집 (join)
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    public String getRefinedText(String rawText) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiProperties.getApiKey());

            String systemInstruction = "너는 집안일 조율을 돕는 다정한 AI 비서야.";
            String userPrompt = String.format(
                    "다음 문장을 기분 나쁘지 않게, 부드럽고 귀여운 '청유형(부탁하는 말투)'으로 바꿔줘. " +
                            "이모지를 적절하게 1개 꼭 넣어줘. " +
                            "바꾼 문장은 최대 200자를 넘으면 안돼. " +
                            "설명 없이 바꾼 문장만 딱 출력해.\n\n" +
                            "문장: \"%s\"",
                    rawText
            );

            OpenAiRequest request = new OpenAiRequest(
                    openAiProperties.getModel(),
                    List.of(
                            new OpenAiRequest.Message("system", systemInstruction),
                            new OpenAiRequest.Message("user", userPrompt)
                    )
            );

            HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

            OpenAiResponse response = restTemplate.postForObject(
                    openAiProperties.getUrl(),
                    entity,
                    OpenAiResponse.class
            );

            if (response != null && !response.choices().isEmpty()) {
                String aiText = response.choices().get(0).message().content();
                log.info("[AI 변환] Success: '{}' -> '{}'", rawText, aiText);
                return aiText;
            }
        } catch (Exception e) {
            log.error("[AI 변환] Error: {}", e.getMessage());
        }
        return rawText; // 실패 시 원본 반환
    }
}
