package com.haoliang.model.vo;

import com.haoliang.model.SysMenu;
import com.haoliang.model.SysRole;
import lombok.Data;

import java.util.List;

@Data
public class RoleVO  extends SysRole {

    private String userStr;

    private String builtIn;

    private List<Integer> menuIds;

    /**
     * 用户拥有的角色信息
     */
    private List<SysMenu> sysMenus;
}
