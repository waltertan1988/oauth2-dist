package org.walter.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.walter.oauth2.properties.OAuth2SecurityProperties;
import org.walter.oauth2.SerializerUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;


@Slf4j
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    private OAuth2SecurityProperties OAuth2SecurityProperties;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 自定义OAuth2重定向的url
     * @param code 授权码
     * @param state 目标资源的url
     * @return 访问令牌AccessToken
     */
    @GetMapping("/redirect")
    @ResponseBody
    public String redirect(HttpServletResponse response,
                           @RequestParam("code") String code,
                           @RequestParam(value = "state") String state){
        String tokenRequestUrl = OAuth2SecurityProperties.getOauth2TokenRequest();
        MultiValueMap<String, String> requestBody = buildRequestBody(code, state, "all");
        HttpHeaders requestHeaders = buildRequestHeader();
        HttpEntity<MultiValueMap> requestEntity = new HttpEntity<>(requestBody, requestHeaders);

        ResponseEntity<OAuth2AccessToken> entity = restTemplate.postForEntity(tokenRequestUrl, requestEntity, OAuth2AccessToken.class);
        if(HttpStatus.OK.equals(entity.getStatusCode())){
            OAuth2AccessToken accessToken = entity.getBody();
            // 把AccessToken写入客户端
            sendAccessTokenToClient(response, accessToken.getValue());
            return String.format("code=%s<br>" +
                    "state=%s<br>" +
                    "token=%s", code, state, SerializerUtil.toJson(accessToken));
        }
        return String.format("%s error<br>" +
                "code=%s<br>" +
                "state=%s", entity.getStatusCode().value(), code, state);
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
        Cookie cookie = new Cookie(OAuth2SecurityProperties.getOauth2TokenCookieKey(), tokenValue);
        cookie.setMaxAge(-1);
        return cookie;
    }

    private HttpHeaders buildRequestHeader(){
        HttpHeaders requestHeaders = new HttpHeaders();
        String clientId = OAuth2SecurityProperties.getOauth2ClientId();
        String clientSecret = OAuth2SecurityProperties.getOauth2ClientSecret();
        String authorizationString = String.format("%s:%s", clientId, clientSecret);
        String authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString(authorizationString.getBytes());
        requestHeaders.add("Authorization", authorizationHeaderValue);
        return requestHeaders;
    }

    private MultiValueMap<String, String> buildRequestBody(String authCode, String state, String scope){
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(OAuth2Utils.GRANT_TYPE, "authorization_code");
        requestBody.add("code", authCode);
        requestBody.add(OAuth2Utils.CLIENT_ID, OAuth2SecurityProperties.getOauth2ClientId());
        requestBody.add("client_secret", OAuth2SecurityProperties.getOauth2ClientSecret());
        requestBody.add(OAuth2Utils.REDIRECT_URI, OAuth2SecurityProperties.getOauth2AuthorizeRequestQueryParams().get(OAuth2Utils.REDIRECT_URI));
        requestBody.add(OAuth2Utils.SCOPE, scope);
        requestBody.add(OAuth2Utils.STATE, state);
        return requestBody;
    }
}
