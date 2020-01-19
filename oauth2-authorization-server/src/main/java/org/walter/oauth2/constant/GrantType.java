package org.walter.oauth2.constant;

import lombok.Getter;

@Getter
public enum GrantType {

    CODE("code", "授权码"),
    CUSTOM("custom", "自定义");

    private String value;
    private String desc;

    GrantType(String value, String desc){
        this.value = value;
        this.desc = desc;
    }
}
