package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCID;
import lombok.Data;

/**
 * @author Dominick Li
 * @description 系统角色表
 **/
@Data
@TableName("sys_role")
public class SysRole extends BaseModelCID {

    public SysRole() { }

    public SysRole(String roleName, String roleCode) {
        this.roleName = roleName;
        this.roleCode = roleCode;
    }
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 角色编码
     */
    private String roleCode;

}
