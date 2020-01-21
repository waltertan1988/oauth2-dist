package org.walter.oauth2.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.stereotype.Component;
import org.walter.oauth2.properties.CustomSecurityProperties;

/**
 * 用自定义的OAuth2授权页面覆盖默认的页面
 */
@Component
public class AuthorizationEndpointPostProcessor implements BeanPostProcessor {
    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    private RedisAuthorizationCodeServices redisAuthorizationCodeServices;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof AuthorizationEndpoint){
            AuthorizationEndpoint endpoint = (AuthorizationEndpoint) bean;
            endpoint.setUserApprovalPage("forward:" + customSecurityProperties.getOauth2ApprovalPage());
            endpoint.setAuthorizationCodeServices(redisAuthorizationCodeServices);
        }

        return bean;
    }
}
