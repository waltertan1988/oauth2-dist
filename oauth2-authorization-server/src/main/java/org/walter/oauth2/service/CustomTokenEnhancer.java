package org.walter.oauth2.service;

import com.google.common.collect.Maps;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;
import org.walter.oauth2.utils.SerializerUtil;

import java.util.Base64;
import java.util.Map;

@Component
public class CustomTokenEnhancer implements TokenEnhancer {
    private final String O_TOKEN_VALUE_KEY = "oTokenValue";
    private final String USERNAME_KEY = "username";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, String> map = Maps.newHashMap();
        map.put(O_TOKEN_VALUE_KEY, accessToken.getValue());
        map.put(USERNAME_KEY, authentication.getUserAuthentication().getName());

        String newTokenValue = Base64.getEncoder().encodeToString(SerializerUtil.toJson(map).getBytes());
        DefaultOAuth2AccessToken wrappedToken = new DefaultOAuth2AccessToken(newTokenValue);
        wrappedToken.setAdditionalInformation(accessToken.getAdditionalInformation());
        wrappedToken.setExpiration(accessToken.getExpiration());
        wrappedToken.setRefreshToken(accessToken.getRefreshToken());
        wrappedToken.setScope(accessToken.getScope());
        wrappedToken.setTokenType(accessToken.getTokenType());
        return wrappedToken;
    }

    public String getUsername(String wrappedToken){
        String wrappedJson = new String(Base64.getDecoder().decode(wrappedToken));
        Map<String, String> map = SerializerUtil.fromJson(wrappedJson, Map.class);
        return map.get(USERNAME_KEY);
    }
}
