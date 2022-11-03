package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCIDNoModifyTime;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 菜单权限表
 * @CreateTime 2022/11/3 10:11
 **/
@Data
@TableName("sys_menu_authority")
public class SysMenuAuthority extends BaseModelCIDNoModifyTime {

    /**
     * 所属菜单Id
     */
    private Integer menuId;

    /**
     * 菜单编码
     */
    private String menuCode;

    /**
     * 权限名称
     */
    private String name;

    /**
     * 权限编码
     */
    private String code;


}
