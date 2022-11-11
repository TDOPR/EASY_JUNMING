package com.haoliang.model.vo;

import lombok.Data;


@Data
public class TokenVO {

    /**
     * 身份证认证token信息
     */
    private String token;



    public TokenVO(String token) {
        this.token = token;
    }


}
