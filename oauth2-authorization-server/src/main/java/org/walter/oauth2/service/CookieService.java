package org.walter.oauth2.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.walter.oauth2.constant.Constants;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Service
public class CookieService {
    /**
     * 把Authentication写入Cookie
     * @param response
     * @param authentication
     * @param domain
     * @param path
     */
    public void addAuthenticationCookie(HttpServletResponse response, Authentication authentication, String domain, String path){
        Cookie cookie = createBase64ValueCookie(Constants.Cookie.AUTHENTICATION_COOKIE_KEY,
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
        return readBase64ValueFromCookie(cookies, Constants.Cookie.AUTHENTICATION_COOKIE_KEY);
    }

    public Cookie createBase64ValueCookie(String key, String value, String domain, String path){
        return createBase64ValueCookie(key, value, domain, path, -1);
    }

    public Cookie createBase64ValueCookie(String key, String value, String domain, String path, int maxAge){
        String cookieValue = Base64.getEncoder().encodeToString(value.getBytes());
        Cookie cookie = new Cookie(key, cookieValue);
        cookie.setMaxAge(maxAge);
        if(StringUtils.hasText(domain)){
            cookie.setDomain(domain);
        }
        if(StringUtils.hasText(path)){
            cookie.setPath(path);
        }

        return cookie;
    }

    public String readBase64ValueFromCookie(Cookie[] cookies, String key){
        if(cookies == null){
            return null;
        }

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(key)) {
                return new String(Base64.getDecoder().decode(cookie.getValue()));
            }
        }

        return null;
    }

    public void deleteCookie(HttpServletRequest request, HttpServletResponse response, String key){
        for (Cookie cookie : request.getCookies()) {
            if(cookie.getName().equals(key)){
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }
}
