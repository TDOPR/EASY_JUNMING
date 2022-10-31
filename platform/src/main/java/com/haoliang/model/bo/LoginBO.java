package com.haoliang.model.bo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginBO {

    @NotEmpty(message = "不能为空")
    private String username;

    @NotEmpty(message = "不能为空")
    private String password;

    private String uuid;

    private String code;

}
