package org.walter.oauth2.properties;

import com.google.common.collect.Maps;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.walter.oauth2.service.ClientInfoService;

import java.util.Collections;
import java.util.Map;

@Configuration
@PropertySource({"classpath:oauth2-security.yml"})
@ConfigurationProperties("oauth2.security")
public class OAuth2SecurityProperties {
    @Autowired
    private ClientInfoService clientInfoService;

    @Value("${oauth2-authorize-request-pattern}")
    private String oauth2AuthorizeRequestPattern;
    @Getter @Value("${oauth2-token-request}")
    private String oauth2TokenRequest;
    @Getter @Value("${oauth2-token-cookie-key}")
    private String oauth2TokenCookieKey;
    @Getter @Value("${oauth2-readAccessToken-request}")
    private String oauth2ReadAccessTokenRequest;
    @Getter @Value("${oauth2-readAuthentication-request}")
    private String oauth2ReadAuthenticationRequest;
    @Getter @Value("${oauth2-removeAccessToken-request}")
    private String oauth2RemoveAccessTokenRequest;

    public String getOauth2AuthorizeRequest(String state){
        return oauth2AuthorizeRequestPattern
                .replace("<FULL_REDIRECT_URI>", clientInfoService.getOauth2RedirectURl())
                .replace("<CLIENT_ID>", clientInfoService.getOauth2ClientId())
                .replace("<STATE>", state)
                .replace("<SCOPE>", clientInfoService.getOauth2Scope());
    }

    // private Map<String, String> oauth2AuthorizeRequestQueryParamMap;
    // public Map<String, String> getOauth2AuthorizeRequestQueryParams(){
    //     if(oauth2AuthorizeRequestQueryParamMap == null){
    //         synchronized (this){
    //             if(oauth2AuthorizeRequestQueryParamMap == null){
    //                 Map<String, String> map = Maps.newHashMap();
    //                 String queryString = getOauth2AuthorizeRequest().split("\\?")[1];
    //                 for(String entryString : queryString.split("&")){
    //                     String[] entry = entryString.split("=");
    //                     if(entry.length == 1){
    //                         map.put(entry[0], entry[0]);
    //                     }else{
    //                         map.put(entry[0], entry[1]);
    //                     }
    //                 }
    //                 oauth2AuthorizeRequestQueryParamMap = Collections.unmodifiableMap(map);
    //             }
    //         }
    //     }
    //     return oauth2AuthorizeRequestQueryParamMap;
    // }
}
