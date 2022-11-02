package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dominick Li
 * @description 系统角色表
 **/
@Data
@TableName("sys_role")
@NoArgsConstructor
public class SysRole extends BaseModelCID {


    public SysRole(String roleName, String roleCode) {
        this.roleName = roleName;
        this.roleCode = roleCode;
    }
    /**
     * 角色名称
     * @required
     */
    private String roleName;
    /**
     * 角色编码
     * @required
     */
    private String roleCode;

}
