package org.walter.oauth2.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource("classpath:security.yml")
@ConfigurationProperties("custom.security")
public class CustomSecurityProperties {
    @Value("${test-uri-pattern}")
    private String testUriPattern;
    @Value("${oauth2-authorize-request}")
    private String oauth2AuthorizeRequest;
    @Value("${oauth2-token-request}")
    private String oauth2TokenRequest;
    @Value("${oauth2-client-id}")
    private String oauth2ClientId;
    @Value("${oauth2-client-secret}")
    private String oauth2ClientSecret;
}
