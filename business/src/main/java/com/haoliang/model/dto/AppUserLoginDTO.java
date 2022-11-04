package com.haoliang.model.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/4 11:18
 **/
@Data
public class AppUserLoginDTO {

    /**
     * 账号 <邮箱或手机号>
     */
    @NotEmpty
    private String username;

    /**
     * 账号类型 1=邮箱, 2=手机号
     */
    @NotEmpty(message = "不能为空")
    private Integer type;

    /**
     * 验证码Id
     */
    @NotEmpty(message = "不能为空")
    private String uuid;

    /**
     * 验证码
     */
    @NotEmpty(message = "不能为空")
    private String code;

}
