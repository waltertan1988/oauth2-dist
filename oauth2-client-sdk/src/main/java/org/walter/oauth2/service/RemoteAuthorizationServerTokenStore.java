package org.walter.oauth2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.walter.oauth2.SerializerUtil;
import org.walter.oauth2.properties.OAuth2SecurityProperties;

import java.util.Collection;

/**
 * 用于操作远程AuthorizationServer的Token的API
 */
@Slf4j
@Component
public class RemoteAuthorizationServerTokenStore implements TokenStore {
    @Autowired
    private OAuth2SecurityProperties OAuth2SecurityProperties;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 通过令牌串，获取OAuth2AccessToken
     * @param requestToken
     * @return
     */
    @Override
    public OAuth2AccessToken readAccessToken(String requestToken) {
        String url = OAuth2SecurityProperties.getOauth2ReadAccessTokenRequest();
        HttpHeaders requestHeaders = buildRequestHeader(requestToken);
        MultiValueMap<String, String> requestBody = buildRequestBody(requestToken);
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if(!HttpStatus.OK.equals(response.getStatusCode())){
            log.error("Fail to fetch OAuth2AccessToken: {}", response.getBody());
            return null;
        }
        return SerializerUtil.deserialize(response.getBody(), OAuth2AccessToken.class);
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        String url = OAuth2SecurityProperties.getOauth2ReadAuthenticationRequest();
        HttpHeaders requestHeaders = buildRequestHeader(token.getValue());
        MultiValueMap<String, String> requestBody = buildRequestBody(token.getValue());
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if(!HttpStatus.OK.equals(response.getStatusCode())){
            log.error("Fail to fetch OAuth2Authentication: {}", response.getBody());
            return null;
        }
        return SerializerUtil.deserialize(response.getBody(), OAuth2Authentication.class);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        String url = OAuth2SecurityProperties.getOauth2RemoveAccessTokenRequest();
        HttpHeaders requestHeaders = buildRequestHeader(token.getValue());
        MultiValueMap<String, String> requestBody = buildRequestBody(token.getValue());
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, requestHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if(!HttpStatus.OK.equals(response.getStatusCode())){
            log.error("Fail to removeAccessToken: {}", response.getBody());
        }
    }

    private MultiValueMap<String, String> buildRequestBody(String tokenValue){
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("token", tokenValue);
        return requestBody;
    }

    private HttpHeaders buildRequestHeader(String tokenValue){
        HttpHeaders requestHeaders = new HttpHeaders();
        String authorizationHeaderValue = String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, tokenValue);
        requestHeaders.add("Authorization", authorizationHeaderValue);
        return requestHeaders;
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        return null;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {

    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {

    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return null;
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return null;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {

    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {

    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        return null;
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        return null;
    }
}
