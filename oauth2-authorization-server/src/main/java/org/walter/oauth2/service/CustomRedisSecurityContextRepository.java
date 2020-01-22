package org.walter.oauth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.walter.oauth2.constant.Constants;
import org.walter.oauth2.properties.CustomSecurityProperties;
import org.walter.oauth2.utils.SerializerUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Component
public class CustomRedisSecurityContextRepository implements SecurityContextRepository {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    private CookieService cookieService;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        SecurityContext context = this.readSecurityContextFromRedis(requestResponseHolder.getRequest());
        if (context == null) {
            context = generateNewContext();
        }
        return context;
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        if(context.getAuthentication() == null || context.getAuthentication() == null){
            return;
        }
        String username = context.getAuthentication().getName();
        String key = String.format(Constants.Cache.Security.AUTHENTICATION_KEY_PATTERN, username);
        String value = SerializerUtil.serialize(context);
        redisTemplate.opsForValue().set(key, value, customSecurityProperties.getRedisSessionAliveMinutes(), TimeUnit.MINUTES);

        cookieService.addAuthenticationCookie(response, context.getAuthentication());
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        String username = cookieService.resolveUsername(request.getCookies());
        String key = String.format(Constants.Cache.Security.AUTHENTICATION_KEY_PATTERN, username);
        return redisTemplate.hasKey(key);
    }

    private SecurityContext readSecurityContextFromRedis(HttpServletRequest request) {
        String username = cookieService.resolveUsername(request.getCookies());
        if(null == username){
            return null;
        }

        String key = String.format(Constants.Cache.Security.AUTHENTICATION_KEY_PATTERN, username);
        String value = redisTemplate.opsForValue().get(key);
        SecurityContext contextFromRedis = SerializerUtil.deserialize(value, SecurityContext.class);
        if (contextFromRedis == null) {
            return null;
        }

        return contextFromRedis;
    }

    protected SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }
}
