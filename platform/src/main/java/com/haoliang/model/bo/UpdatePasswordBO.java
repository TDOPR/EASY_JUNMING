package com.haoliang.model.bo;

import lombok.Data;

/**
 * @author Dominick Li
 * @description 修改密码
 **/
@Data
public class UpdatePasswordBO {
    /**
     * 用户Id
     */
    private Integer userId;
    /**
     * 原密码
     */
    private String oldPassword;
    /**
     * 新密码
     */
    private String password;

}
