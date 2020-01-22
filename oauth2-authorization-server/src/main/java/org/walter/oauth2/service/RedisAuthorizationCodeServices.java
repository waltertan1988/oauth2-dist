package org.walter.oauth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;
import org.walter.oauth2.constant.Constants;
import org.walter.oauth2.properties.CustomSecurityProperties;
import org.walter.oauth2.utils.SerializerUtil;

import java.time.Duration;

@Service
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private CustomSecurityProperties customSecurityProperties;

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        String key = String.format(Constants.Cache.Security.AUTHORIZATION_CODE_PATTERN, code);
        String value = SerializerUtil.serialize(authentication);
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(customSecurityProperties.getOauth2AuthCodeAliveSeconds()));
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        String key = String.format(Constants.Cache.Security.AUTHORIZATION_CODE_PATTERN, code);
        String value = redisTemplate.opsForValue().get(key);
        OAuth2Authentication oAuth2Authentication = SerializerUtil.deserialize(value, OAuth2Authentication.class);
        redisTemplate.delete(key);
        return oAuth2Authentication;
    }
}
