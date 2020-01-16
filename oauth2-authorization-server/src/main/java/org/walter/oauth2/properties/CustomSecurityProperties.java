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
    @Value("${login-page-uri}")
    private String loginPageUri;
}
