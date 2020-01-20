package org.walter.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.walter.oauth2.properties.CustomSecurityProperties;
import org.walter.oauth2.service.CustomHttp403ForbiddenEntryPoint;
import org.walter.oauth2.service.CustomOauth2AuthenticationSuccessHandler;

@Order(2) //保证基本的身份认证处理在oauth2授权前已经执行
@Configuration
@EnableAuthorizationServer
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    private CustomOauth2AuthenticationSuccessHandler customOauth2AuthenticationSuccessHandler;
    @Autowired
    private CustomHttp403ForbiddenEntryPoint customHttp403ForbiddenEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.
            formLogin()
                .loginPage(customSecurityProperties.getLoginPageUri())
                .successHandler(customOauth2AuthenticationSuccessHandler)
                .and()
            .authorizeRequests()
                .antMatchers("/error", customSecurityProperties.getTestUriPattern(), customSecurityProperties.getLoginPageUri())
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .exceptionHandling()
                // 自定义无权限时的处理行为
                .authenticationEntryPoint(customHttp403ForbiddenEntryPoint)
                .and()
            .csrf()
                .disable();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
