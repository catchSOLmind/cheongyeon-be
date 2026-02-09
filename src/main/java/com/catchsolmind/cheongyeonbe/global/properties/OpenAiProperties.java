package com.catchsolmind.cheongyeonbe.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "openai")
public class OpenAiProperties {
    private String apiKey;

    private String model;

    private String url;
}
