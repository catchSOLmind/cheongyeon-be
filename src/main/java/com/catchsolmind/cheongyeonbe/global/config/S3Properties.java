package com.catchsolmind.cheongyeonbe.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "cloud.aws.s3")
public class S3Properties {
    private String bucket;
    private String region;
    private String baseUrl;
}
