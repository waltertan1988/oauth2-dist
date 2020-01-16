package org.walter.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.walter.oauth2.properties.CustomSecurityProperties;

@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    @Qualifier("customOauth2AuthenticationSuccessHandler")
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.
            formLogin()
                .loginPage(customSecurityProperties.getLoginPageUri())
                .successHandler(authenticationSuccessHandler)
                .and()
            .authorizeRequests()
                .antMatchers(customSecurityProperties.getTestUriPattern(), customSecurityProperties.getLoginPageUri())
                    .permitAll()
                .anyRequest()
                    .authenticated()
                .and()
            .csrf()
                .disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
