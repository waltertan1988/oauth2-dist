package org.walter.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.walter.oauth2.service.CustomApprovalStoreUserApprovalHandler;
import org.walter.oauth2.service.CustomTokenEnhancer;
import org.walter.oauth2.service.RedisAuthorizationCodeServices;
import org.walter.oauth2.service.UserDetailsServiceImpl;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private RedisAuthorizationCodeServices redisAuthorizationCodeServices;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private CustomTokenEnhancer customTokenEnhancer;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(jdbcClientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authorizationCodeServices(redisAuthorizationCodeServices)
                .tokenStore(redisTokenStore())
                .setClientDetailsService(jdbcClientDetailsService());

        ApprovalStoreUserApprovalHandler innerUserApprovalHandler =
                (ApprovalStoreUserApprovalHandler)endpoints.getUserApprovalHandler();

        endpoints.userApprovalHandler(customApprovalStoreUserApprovalHandler(innerUserApprovalHandler))
                .tokenEnhancer(customTokenEnhancer)
                .userDetailsService(userDetailsService);
    }

    public UserApprovalHandler customApprovalStoreUserApprovalHandler(ApprovalStoreUserApprovalHandler innerUserApprovalHandler){
        UserApprovalHandler handler = new CustomApprovalStoreUserApprovalHandler(innerUserApprovalHandler);
        return handler;
    }

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    @Bean
    public ClientDetailsService jdbcClientDetailsService(){
        return new JdbcClientDetailsService(dataSource);
    }
}
