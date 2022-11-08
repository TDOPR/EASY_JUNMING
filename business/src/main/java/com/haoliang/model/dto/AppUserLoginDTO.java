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
     * 邮箱账号
     * @required
     */
    @NotEmpty
    private String email;

    /**
     * 邀请码
     */
    private String inviteCode;


    /**
     * 验证码Id
     * @required
     */
    @NotEmpty(message = "不能为空")
    private String uuid;

    /**
     * 验证码
     * @required
     */
    @NotEmpty(message = "不能为空")
    private String code;

}
