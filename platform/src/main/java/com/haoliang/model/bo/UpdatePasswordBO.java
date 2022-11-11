package com.haoliang.model.bo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author Dominick Li
 * @description 修改密码
 **/
@Data
public class UpdatePasswordBO {

    /**
     * 用户Id
     * @ignore
     */
    private Integer userId;

    /**
     * 原密码
     */
    @NotEmpty
    private String oldPassword;

    /**
     * 新密码
     */
    @NotEmpty
    private String password;

}
