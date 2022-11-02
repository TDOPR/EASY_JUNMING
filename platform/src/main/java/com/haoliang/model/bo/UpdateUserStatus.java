package com.haoliang.model.bo;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/10/26 18:34
 **/
@Data
public class UpdateUserStatus {

    /**
     * 用户状态
     */
    private Integer id;

    /**
     * 状态标识 1=启用 0=禁用
     */
    private Integer enabled;

}
