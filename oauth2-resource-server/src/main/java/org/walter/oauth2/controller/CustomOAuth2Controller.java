package org.walter.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.walter.oauth2.AbstractOAuth2Controller;
import org.walter.oauth2.SerializerUtil;
import org.walter.oauth2.properties.OAuth2SecurityProperties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
public class CustomOAuth2Controller extends AbstractOAuth2Controller {
    @Autowired
    private OAuth2SecurityProperties oAuth2SecurityProperties;

    /**
     * 自定义OAuth2重定向的url
     * @param code 授权码
     * @param state 目标资源的url
     * @return 访问令牌AccessToken
     */
    @Override
    protected String handleOAuth2AccessToken(HttpServletResponse response, String code, String state, OAuth2AccessToken accessToken) {
        // 把AccessToken写入客户端
        sendAccessTokenToClient(response, accessToken.getValue());
        return String.format("code=%s<br>" +
                "state=%s<br>" +
                "token=%s", code, state, SerializerUtil.toJson(accessToken));
    }

    /**
     * 把AccessToken回传到客户端
     * @param response
     * @param tokenValue
     */
    protected void sendAccessTokenToClient(HttpServletResponse response, String tokenValue){
        response.addCookie(createAccessTokenCookie(tokenValue));
    }

    private Cookie createAccessTokenCookie(String tokenValue){
        Cookie cookie = new Cookie(oAuth2SecurityProperties.getOauth2TokenCookieKey(), tokenValue);
        cookie.setMaxAge(-1);
        return cookie;
    }
}
