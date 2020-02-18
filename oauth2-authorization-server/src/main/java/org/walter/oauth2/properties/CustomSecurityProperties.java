package org.walter.oauth2.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource("classpath:security.yml")
@ConfigurationProperties("custom.security")
public class CustomSecurityProperties {
    @Value("${redis-session-alive-minutes}")
    private Long redisSessionAliveMinutes;
    @Value("${test-uri-pattern}")
    private String testUriPattern;
    @Value("${login-page-uri}")
    private String loginPageUri;
    @Value("${oauth2-grant-type}")
    private String oauth2GrantType;
    @Value("${oauth2-approval-request}")
    private String oauth2ApprovalRequest;
    @Value("${oauth2-authcode-alive-seconds}")
    private Long oauth2AuthCodeAliveSeconds;
}
