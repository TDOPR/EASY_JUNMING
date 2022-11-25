package com.haoliang.model.vo;

import lombok.Data;


@Data
public class TokenVO {

    /**
     * 身份证认证token信息
     */
    private String token;

    /**
     * websocket连接地址
     */
    private String wsAddress;

    public TokenVO(String token, String wsAddress) {
        this.token = token;
        this.wsAddress=wsAddress;
    }


}
