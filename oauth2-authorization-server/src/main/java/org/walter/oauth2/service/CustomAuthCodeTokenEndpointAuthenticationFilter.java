package org.walter.oauth2.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.walter.oauth2.constant.Constants;
import org.walter.oauth2.SerializerUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Objects;

@Slf4j
public class CustomAuthCodeTokenEndpointAuthenticationFilter extends TokenEndpointAuthenticationFilter {

    private RedisTemplate<String, String> redisTemplate;

    private ClientDetailsService clientDetailsService;

    public CustomAuthCodeTokenEndpointAuthenticationFilter(ClientDetailsService clientDetailsService, OAuth2RequestFactory oAuth2RequestFactory, RedisTemplate<String, String> redisTemplate) {
        super(new NoOpAuthenticationManager(), oAuth2RequestFactory);
        this.clientDetailsService = clientDetailsService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据授权码，从Redis获取AuthorizationServer的Authentication
     * @param request
     * @return
     */
    @Override
    protected Authentication extractCredentials(HttpServletRequest request) {
        String clientId = request.getParameter(OAuth2Utils.CLIENT_ID);
        String clientSecret = request.getParameter("client_secret");
        if(clientId == null || !isClientInfoMatched(clientId, clientSecret)){
            return null;
        }

        saveClientAuthentication(clientId);

        String grantType = request.getParameter(OAuth2Utils.GRANT_TYPE);
        if (grantType != null && grantType.equals("authorization_code")) {
            String authCode = request.getParameter("code");
            String key = String.format(Constants.Cache.Security.AUTHORIZATION_CODE_PATTERN, authCode);
            String value = redisTemplate.opsForValue().get(key);
            OAuth2Authentication oAuth2Authentication = SerializerUtil.deserialize(value, OAuth2Authentication.class);
            if(oAuth2Authentication != null){
                return oAuth2Authentication.getUserAuthentication();
            }
        }
        return null;
    }

    private void saveClientAuthentication(String clientId){
        Authentication authentication = new ClientAuthentication(clientId);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    /**
     * 检查clientId和clientSecret是否匹配
     * @param clientId
     * @param clientSecret
     * @return
     */
    private boolean isClientInfoMatched(String clientId, String clientSecret){
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if(null == clientDetails || !Objects.equals(clientDetails.getClientSecret(), clientSecret)){
            log.error("clientId [{}] and clientSecret [{}] are not matched.", clientId, clientSecret);
            return false;
        }
        return true;
    }

    @AllArgsConstructor
    private static class ClientAuthentication implements Authentication{

        private String clientId;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

        }

        @Override
        public String getName() {
            return clientId;
        }
    }

    private static class NoOpAuthenticationManager implements AuthenticationManager {
        public Authentication authenticate(Authentication authentication)
                throws AuthenticationException {
            return authentication;
        }
    }
}
