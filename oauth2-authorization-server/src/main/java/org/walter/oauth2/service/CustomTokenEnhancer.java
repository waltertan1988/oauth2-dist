package org.walter.oauth2.service;

import com.google.common.collect.Maps;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;
import org.walter.oauth2.SerializerUtil;

import java.util.Base64;
import java.util.Map;

@Component
public class CustomTokenEnhancer implements TokenEnhancer {
    private final String O_ACCESS_TOKEN_VALUE_KEY = "oAccessTokenValue";
    private final String O_REFRESH_TOKEN_VALUE_KEY = "oRefreshTokenValue";
    private final String USERNAME_KEY = "username";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String newTokenValue = wrapAccessTokenValue(accessToken, authentication, false);
        DefaultOAuth2AccessToken wrappedToken = new DefaultOAuth2AccessToken(newTokenValue);
        wrappedToken.setAdditionalInformation(accessToken.getAdditionalInformation());
        wrappedToken.setExpiration(accessToken.getExpiration());
        wrappedToken.setRefreshToken(wrapRefreshToken(accessToken, authentication));
        wrappedToken.setScope(accessToken.getScope());
        wrappedToken.setTokenType(accessToken.getTokenType());
        return wrappedToken;
    }

    protected String wrapAccessTokenValue(OAuth2AccessToken accessToken, OAuth2Authentication authentication, boolean isRefreshToken){
        Map<String, String> map = Maps.newHashMap();
        map.put(USERNAME_KEY, authentication.getUserAuthentication().getName());
        if(isRefreshToken){
            map.put(O_REFRESH_TOKEN_VALUE_KEY, accessToken.getRefreshToken().getValue());
        }else{
            map.put(O_ACCESS_TOKEN_VALUE_KEY, accessToken.getValue());
        }
        return Base64.getEncoder().encodeToString(SerializerUtil.toJson(map).getBytes());
    }

    protected OAuth2RefreshToken wrapRefreshToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication){
        String newTokenValue = wrapAccessTokenValue(accessToken, authentication, true);
        OAuth2RefreshToken oRefreshToken = accessToken.getRefreshToken();
        if(oRefreshToken instanceof DefaultExpiringOAuth2RefreshToken){
            return new DefaultExpiringOAuth2RefreshToken(newTokenValue, ((DefaultExpiringOAuth2RefreshToken) oRefreshToken).getExpiration());
        }else{
            return new DefaultOAuth2RefreshToken(newTokenValue);
        }
    }

    /**
     * 从AccessToken或RefreshToken中获取username
     * @param wrappedToken
     * @return
     */
    public String getUsername(String wrappedToken){
        String wrappedJson = new String(Base64.getDecoder().decode(wrappedToken));
        Map<String, String> map = SerializerUtil.fromJson(wrappedJson, Map.class);
        return map.get(USERNAME_KEY);
    }
}
