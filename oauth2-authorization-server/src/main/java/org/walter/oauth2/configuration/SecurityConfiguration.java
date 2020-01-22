package org.walter.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.walter.oauth2.properties.CustomSecurityProperties;
import org.walter.oauth2.service.CustomHttp403ForbiddenEntryPoint;
import org.walter.oauth2.service.CustomOauth2AuthenticationSuccessHandler;
import org.walter.oauth2.service.CustomRedisSecurityContextRepository;

@Order(-1)
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    private CustomOauth2AuthenticationSuccessHandler customOauth2AuthenticationSuccessHandler;
    @Autowired
    private CustomHttp403ForbiddenEntryPoint customHttp403ForbiddenEntryPoint;
    @Autowired
    private CustomRedisSecurityContextRepository customRedisSecurityContextRepository;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
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
                customSecurityProperties.getTestUriPattern(),
                customSecurityProperties.getLoginPageUri()
        };
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
