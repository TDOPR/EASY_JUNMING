package com.haoliang.model.dto;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/10/26 18:34
 **/
@Data
public class UpdateUserStatusDTO {

    /**
     * 用户状态
     */
    private Integer id;

    /**
     * 状态标识 1=启用 0=禁用
     */
    private Integer enabled;

}
