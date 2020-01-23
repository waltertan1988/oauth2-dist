package org.walter.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.walter.oauth2.properties.CustomSecurityProperties;
import org.walter.oauth2.service.CustomAuthCodeTokenEndpointAuthenticationFilter;
import org.walter.oauth2.service.CustomHttp403ForbiddenEntryPoint;
import org.walter.oauth2.service.CustomOauth2AuthenticationSuccessHandler;
import org.walter.oauth2.service.CustomRedisSecurityContextRepository;

@Order(-1)
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private CustomOauth2AuthenticationSuccessHandler customOauth2AuthenticationSuccessHandler;
    @Autowired
    private CustomHttp403ForbiddenEntryPoint customHttp403ForbiddenEntryPoint;
    @Autowired
    private CustomRedisSecurityContextRepository customRedisSecurityContextRepository;
    @Autowired
    @Qualifier("jdbcClientDetailsService")
    private ClientDetailsService clientDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            // 注册TokenEndpoint的认证过滤器，保证请求在进入TokenEndpoint前已经认证过
            .addFilterBefore(customAuthCodeTokenEndpointAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .securityContext()
                // 使用Redis代替默认的HttpSession来保存SecurityContext
                .securityContextRepository(customRedisSecurityContextRepository)
                .and()
            .formLogin()
                .loginPage(customSecurityProperties.getLoginPageUri())
                .successHandler(customOauth2AuthenticationSuccessHandler)
                .and()
            .authorizeRequests()
                .antMatchers(permitAntPatterns()).permitAll()
                .anyRequest().authenticated()
                .and()
            .exceptionHandling()
                // 自定义无权限时的处理行为
                .authenticationEntryPoint(customHttp403ForbiddenEntryPoint)
                .and()
            .csrf()
                .disable();
    }

    private String[] permitAntPatterns(){
        return new String[]{
                "/error",
                "/oauth2/readAccessToken",
                "/oauth2/readAuthentication",
                "/oauth2/removeAccessToken",
                customSecurityProperties.getTestUriPattern(),
                customSecurityProperties.getLoginPageUri()
        };
    }

    private CustomAuthCodeTokenEndpointAuthenticationFilter customAuthCodeTokenEndpointAuthenticationFilter(){
        return new CustomAuthCodeTokenEndpointAuthenticationFilter(clientDetailsService, oAuth2RequestFactory(), redisTemplate);
    }

    @Bean
    public OAuth2RequestFactory oAuth2RequestFactory(){
        return new DefaultOAuth2RequestFactory(clientDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
