package org.walter.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {
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
}
