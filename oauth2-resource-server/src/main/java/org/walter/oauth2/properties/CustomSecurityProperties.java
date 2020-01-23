package org.walter.oauth2.properties;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;
import java.util.Map;

@Configuration
@PropertySource("classpath:security.yml")
@ConfigurationProperties("custom.security")
public class CustomSecurityProperties {
    @Getter @Value("${test-uri-pattern}")
    private String testUriPattern;
    @Getter @Value("${oauth2-authorize-request}")
    private String oauth2AuthorizeRequest;
    @Getter @Value("${oauth2-token-request}")
    private String oauth2TokenRequest;
    @Getter @Value("${oauth2-client-id}")
    private String oauth2ClientId;
    @Getter @Value("${oauth2-client-secret}")
    private String oauth2ClientSecret;
    @Getter @Value("${oauth2-token-cookie-key}")
    private String oauth2TokenCookieKey;
    @Getter @Value("${oauth2-readAccessToken-request}")
    private String oauth2ReadAccessTokenRequest;
    @Getter @Value("${oauth2-readAuthentication-request}")
    private String oauth2ReadAuthenticationRequest;
    @Getter @Value("${oauth2-removeAccessToken-request}")
    private String oauth2RemoveAccessTokenRequest;

    private Map<String, String> oauth2AuthorizeRequestQueryParamMap;
    public Map<String, String> getOauth2AuthorizeRequestQueryParams(){
        if(oauth2AuthorizeRequestQueryParamMap == null){
            synchronized (this){
                if(oauth2AuthorizeRequestQueryParamMap == null){
                    Map<String, String> map = Maps.newHashMap();
                    String queryString = oauth2AuthorizeRequest.split("\\?")[1];
                    for(String entryString : queryString.split("&")){
                        String[] entry = entryString.split("=");
                        if(entry.length == 1){
                            map.put(entry[0], entry[0]);
                        }else{
                            map.put(entry[0], entry[1]);
                        }
                    }
                    oauth2AuthorizeRequestQueryParamMap = Collections.unmodifiableMap(map);
                }
            }
        }
        return oauth2AuthorizeRequestQueryParamMap;
    }
}
