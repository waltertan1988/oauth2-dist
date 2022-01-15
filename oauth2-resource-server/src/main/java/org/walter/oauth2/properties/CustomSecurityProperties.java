package org.walter.oauth2.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.walter.oauth2.service.ClientInfoService;

@Configuration
@PropertySource({"classpath:custom-security.yml"})
@ConfigurationProperties("custom.security")
public class CustomSecurityProperties implements ClientInfoService {
    @Getter @Value("${test-uri-pattern}")
    private String testUriPattern;
    @Getter @Value("${oauth2-client-id}")
    private String oauth2ClientId;
    @Getter @Value("${oauth2-client-secret}")
    private String oauth2ClientSecret;
    @Getter @Value("${oauth2-redirect-url}")
    private String oauth2RedirectURl;
    @Getter @Value("${oauth2-scope}")
    private String oauth2Scope;
}
