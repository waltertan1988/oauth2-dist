package org.walter.oauth2.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;
import org.walter.oauth2.properties.CustomSecurityProperties;

import java.time.Duration;

@Service
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CustomSecurityProperties customSecurityProperties;

    @Override
    @SneakyThrows
    protected void store(String code, OAuth2Authentication authentication) {
        String value = new ObjectMapper().writeValueAsString(authentication);
        redisTemplate.opsForValue().set(code, value, Duration.ofSeconds(customSecurityProperties.getOauth2AuthCodeAliveSeconds()));
    }

    @Override
    @SneakyThrows
    protected OAuth2Authentication remove(String code) {
        String value = String.valueOf(redisTemplate.opsForValue().get(code));
        OAuth2Authentication oAuth2Authentication = new ObjectMapper().readValue(value, new TypeReference<OAuth2Authentication>(){});
        redisTemplate.delete(code);
        return oAuth2Authentication;
    }
}
