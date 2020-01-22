package org.walter.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.walter.oauth2.properties.CustomSecurityProperties;

import java.util.Base64;


@Slf4j
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 自定义OAuth2重定向的url
     * @param code 授权码
     * @param state 目标资源的url
     * @return 访问令牌AccessToken
     */
    @GetMapping("/redirect")
    @ResponseBody
    public String redirect(@RequestParam("code") String code, @RequestParam(value = "state") String state){
        log.info("code: {}, state: {}", code, state);

        String tokenRequestUrl = customSecurityProperties.getOauth2TokenRequest();
        MultiValueMap<String, String> requestBody = buildRequestBody(code, state, "all");
        HttpHeaders requestHeaders = buildRequestHeader();
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, requestHeaders);

        ResponseEntity<OAuth2AccessToken> entity = restTemplate.postForEntity(tokenRequestUrl, requestEntity, OAuth2AccessToken.class);
        if(HttpStatus.OK.equals(entity.getStatusCode())){
            OAuth2AccessToken accessToken = entity.getBody();

            return code + "|" + state + "|" + accessToken.getValue();
        }

        return entity.getStatusCode().value() + ": Fail for " + code + "|" + state;
    }

    private HttpHeaders buildRequestHeader(){
        HttpHeaders requestHeaders = new HttpHeaders();
        String clientId = customSecurityProperties.getOauth2ClientId();
        String clientSecret = customSecurityProperties.getOauth2ClientSecret();
        String authorizationString = String.format("%s:%s", clientId, clientSecret);
        String authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString(authorizationString.getBytes());
        requestHeaders.add("Authorization", authorizationHeaderValue);
        return requestHeaders;
    }

    private MultiValueMap<String, String> buildRequestBody(String authCode, String redirectUri, String scope){
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "authorization_code");
        requestBody.add("code", authCode);
        requestBody.add("client_id", customSecurityProperties.getOauth2ClientId());
        requestBody.add("client_secret", customSecurityProperties.getOauth2ClientSecret());
        requestBody.add("redirect_uri", redirectUri);
        requestBody.add("scope", scope);
        return  requestBody;
    }
}
