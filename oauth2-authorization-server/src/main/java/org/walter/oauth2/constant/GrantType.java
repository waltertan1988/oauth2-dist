package org.walter.oauth2.constant;

import lombok.Getter;

@Getter
public enum GrantType {

    GRANT_CODE("grantCode", "授权码模式"),
    PASSWORD("password", "密码模式");

    private String value;
    private String desc;

    GrantType(String value, String desc){
        this.value = value;
        this.desc = desc;
    }
}
