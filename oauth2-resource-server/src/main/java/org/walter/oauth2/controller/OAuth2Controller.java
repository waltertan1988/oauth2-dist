package org.walter.oauth2.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Slf4j
@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {
    /**
     * 自定义OAuth2重定向的url
     * @param code 授权码
     * @param state 目标资源的url
     * @return
     */
    @GetMapping("/redirect")
    @ResponseBody
    public String redirect(@RequestParam("code") String code, @RequestParam(value = "state") String state){
        log.info("code: {}, state: {}", code, state);
        return code + "|" + state;
    }
}
