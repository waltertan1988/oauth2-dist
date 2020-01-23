package org.walter.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.walter.oauth2.utils.SerializerUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {
    @Autowired
    private RedisTokenStore redisTokenStore;

    /**
     * 自定义OAuth2授权页面
     * @param request
     * @param model
     * @return
     */
    @GetMapping("/approval")
    public String confirmAccess(HttpServletRequest request, Map<String, Object> model) {
        log.info(">>>>>> authorizationRequest: {}", request.getAttribute("authorizationRequest"));
        log.info(">>>>>> response_type: {}", request.getAttribute("response_type"));
        log.info(">>>>>> redirect_uri: {}", request.getAttribute("redirect_uri"));
        log.info(">>>>>> scopes: {}", request.getAttribute("scopes"));
        log.info(">>>>>> client_id: {}", request.getAttribute("client_id"));
        log.info(">>>>>> scope: {}", request.getAttribute("scope"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        model.put("auth", authentication);
        log.info(">>>>>> authentication: {}", authentication);

        LinkedHashMap<String, String> linkedHashMap = (LinkedHashMap<String, String>) request.getAttribute("scopes");
        model.put("scopes", linkedHashMap.keySet());

        return "/oauth2/approval";
    }

    @GetMapping("/readAccessToken")
    public ResponseEntity<String> readAccessToken(@RequestParam("token") String token){
        OAuth2AccessToken oAuth2AccessToken;

        try{
            oAuth2AccessToken = redisTokenStore.readAccessToken(token);
        }catch (Exception e){
            log.error("[readAccessToken] Fail", e);
            return getResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (oAuth2AccessToken == null) {
            return getResponse("Token was not recognised", HttpStatus.UNAUTHORIZED);
        }

        if (oAuth2AccessToken.isExpired()) {
            return getResponse("Token has expired", HttpStatus.UNAUTHORIZED);
        }

        return getResponse(SerializerUtil.serialize(oAuth2AccessToken), HttpStatus.OK);
    }

    @GetMapping("/readAuthentication")
    public ResponseEntity<String> readAuthentication(@RequestParam("token") String token){
        OAuth2Authentication oAuth2Authentication;

        try{
            oAuth2Authentication = redisTokenStore.readAuthentication(token);
        }catch (Exception e){
            log.error("[readAuthentication] Fail", e);
            return getResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(oAuth2Authentication == null){
            return getResponse("OAuth2Authentication was not recognised", HttpStatus.UNAUTHORIZED);
        }

        return getResponse(SerializerUtil.serialize(oAuth2Authentication), HttpStatus.OK);
    }

    @GetMapping("/removeAccessToken")
    public ResponseEntity<String> removeAccessToken(@RequestParam("token") String token){
        try{
            redisTokenStore.removeAccessToken(token);
        }catch (Exception e){
            log.error("[removeAccessToken] Fail", e);
            return getResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return getResponse("success", HttpStatus.OK);
    }

    private ResponseEntity<String> getResponse(String object, HttpStatus httpStatus) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<>(object, headers, httpStatus);
    }
}
