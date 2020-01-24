package org.walter.oauth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.walter.oauth2.properties.CustomSecurityProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Service
public class CookieService {
    @Autowired
    private CustomSecurityProperties customSecurityProperties;

    /**
     * 把Authentication写入Cookie
     * @param response
     * @param authentication
     * @param domain
     * @param path
     */
    public void addAuthenticationCookie(HttpServletResponse response, Authentication authentication, String domain, String path){
        Cookie cookie = createBase64ValueCookie(customSecurityProperties.getAuthenticationCookieKey(),
                authentication.getName(), domain, path);
        response.addCookie(cookie);
    }

    /**
     * 把Authentication写入Cookie
     * @param response
     * @param authentication
     */
    public void addAuthenticationCookie(HttpServletResponse response, Authentication authentication){
        addAuthenticationCookie(response, authentication, null, null);
    }

    /**
     * 从Cookie解析出本应用的username
     * @param cookies
     * @return
     */
    public String resolveUsername(Cookie[] cookies){
        String username = readBase64ValueFromCookie(cookies, customSecurityProperties.getAuthenticationCookieKey());
        return username;
    }


    private Cookie createBase64ValueCookie(String key, String value, String domain, String path){
        String cookieValue = Base64.getEncoder().encodeToString(value.getBytes());
        Cookie cookie = new Cookie(key, cookieValue);
        cookie.setMaxAge(-1);
        if(StringUtils.hasText(domain)){
            cookie.setDomain(domain);
        }
        if(StringUtils.hasText(path)){
            cookie.setPath(path);
        }

        return cookie;
    }

    private String readBase64ValueFromCookie(Cookie[] cookies, String key){
        if(cookies == null){
            return null;
        }

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(key))
                return new String(Base64.getDecoder().decode(cookie.getValue()));
        }

        return null;
    }
}
