package org.walter.oauth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
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
     */
    public void addAuthenticationCookie(HttpServletResponse response, Authentication authentication){
        Cookie cookie = createBase64ValueCookie(customSecurityProperties.getAuthenticationCookieKey(),
                authentication.getName());
        response.addCookie(cookie);
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


    private Cookie createBase64ValueCookie(String key, String value){
        String cookieValue = Base64.getEncoder().encodeToString(value.getBytes());
        Cookie cookie = new Cookie(key, cookieValue);
        cookie.setMaxAge(-1);
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
