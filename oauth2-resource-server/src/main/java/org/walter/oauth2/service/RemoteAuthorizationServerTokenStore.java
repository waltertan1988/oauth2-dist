package org.walter.oauth2.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.walter.oauth2.properties.CustomSecurityProperties;
import org.walter.oauth2.utils.SerializerUtil;

import java.util.Collection;

/**
 * 用于操作远程AuthorizationServer的Token的API
 */
@Slf4j
@Component
public class RemoteAuthorizationServerTokenStore implements TokenStore {
    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 通过令牌串，获取OAuth2AccessToken
     * @param requestToken
     * @return
     */
    @Override
    public OAuth2AccessToken readAccessToken(String requestToken) {
        String url = customSecurityProperties.getOauth2ReadAccessTokenRequest() + "?token=" + requestToken;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if(!HttpStatus.OK.equals(response.getStatusCode())){
            log.error("Fail to fetch OAuth2AccessToken: {}", response.getBody());
            return null;
        }
        return SerializerUtil.deserialize(response.getBody(), OAuth2AccessToken.class);
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        String url = customSecurityProperties.getOauth2ReadAuthenticationRequest() + "?token=" + token.getValue();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if(!HttpStatus.OK.equals(response.getStatusCode())){
            log.error("Fail to fetch OAuth2Authentication: {}", response.getBody());
            return null;
        }
        return SerializerUtil.deserialize(response.getBody(), OAuth2Authentication.class);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        String url = customSecurityProperties.getOauth2RemoveAccessTokenRequest() + "?token=" + token.getValue();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if(!HttpStatus.OK.equals(response.getStatusCode())){
            log.error("Fail to removeAccessToken: {}", response.getBody());
        }
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
