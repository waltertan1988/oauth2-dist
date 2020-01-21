package org.walter.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.walter.oauth2.properties.CustomSecurityProperties;
import org.walter.oauth2.service.CustomHttp403ForbiddenEntryPoint;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
    @Autowired
    private CustomSecurityProperties customSecurityProperties;
    @Autowired
    private CustomHttp403ForbiddenEntryPoint customHttp403ForbiddenEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
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
                "/error", "/oauth2/redirect",
                customSecurityProperties.getTestUriPattern()
        };
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
    }
}
