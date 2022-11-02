package com.haoliang.model.bo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginBO {

    /**
     * 用户名
     */
    @NotEmpty(message = "不能为空")
    private String username;

    /**
     * 密码
     */
    @NotEmpty(message = "不能为空")
    private String password;

    /**
     * 验证码id 可选
     */
    private String uuid;

    /**
     * 验证码填写内容
     */
    private String code;

}