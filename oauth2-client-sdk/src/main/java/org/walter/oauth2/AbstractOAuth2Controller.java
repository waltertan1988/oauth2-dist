package org.walter.oauth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.walter.oauth2.properties.OAuth2SecurityProperties;
import org.walter.oauth2.service.ClientInfoService;

import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Slf4j
@RequestMapping("/oauth2")
public abstract class AbstractOAuth2Controller {
    @Autowired
    protected OAuth2SecurityProperties oAuth2SecurityProperties;
    @Autowired
    protected RestTemplate restTemplate;
    @Autowired
    protected ClientInfoService clientInfoService;

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
        String tokenRequestUrl = oAuth2SecurityProperties.getOauth2TokenRequest();
        HttpEntity<MultiValueMap> requestEntity = buildRequestEntity(code, state, clientInfoService);
        ResponseEntity<OAuth2AccessToken> responseEntity = restTemplate.postForEntity(tokenRequestUrl, requestEntity, OAuth2AccessToken.class);
        if(HttpStatus.OK.equals(responseEntity.getStatusCode())){
            OAuth2AccessToken accessToken = responseEntity.getBody();
            return handleOAuth2AccessToken(response, code, state, accessToken);
        }else{
            return String.format("%s error<br>" +
                    "code=%s<br>" +
                    "state=%s", responseEntity.getStatusCode().value(), code, state);
        }
    }

    protected abstract String handleOAuth2AccessToken(HttpServletResponse response,
                                                      String code, String state,
                                                      OAuth2AccessToken accessToken);

    protected HttpEntity<MultiValueMap> buildRequestEntity(String code, String state, ClientInfoService clientInfoService){
        MultiValueMap<String, String> requestBody = buildRequestBody(code, state, clientInfoService);
        HttpHeaders requestHeaders = buildRequestHeader(clientInfoService);
        return new HttpEntity<>(requestBody, requestHeaders);
    }

    private MultiValueMap<String, String> buildRequestBody(String authCode, String state, ClientInfoService clientInfoService){
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add(OAuth2Utils.GRANT_TYPE, "authorization_code");
        requestBody.add("code", authCode);
        requestBody.add(OAuth2Utils.CLIENT_ID, clientInfoService.getOauth2ClientId());
        requestBody.add("client_secret", clientInfoService.getOauth2ClientSecret());
        requestBody.add(OAuth2Utils.REDIRECT_URI, clientInfoService.getOauth2RedirectURl());
        requestBody.add(OAuth2Utils.SCOPE, clientInfoService.getOauth2Scope());
        requestBody.add(OAuth2Utils.STATE, state);
        return requestBody;
    }

    private HttpHeaders buildRequestHeader(ClientInfoService clientInfoService){
        HttpHeaders requestHeaders = new HttpHeaders();
        String clientId = clientInfoService.getOauth2ClientId();
        String clientSecret = clientInfoService.getOauth2ClientSecret();
        String authorizationString = String.format("%s:%s", clientId, clientSecret);
        String authorizationHeaderValue = "Basic " + Base64.getEncoder().encodeToString(authorizationString.getBytes());
        requestHeaders.add("Authorization", authorizationHeaderValue);
        return requestHeaders;
    }
}
