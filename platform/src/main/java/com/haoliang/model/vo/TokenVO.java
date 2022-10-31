package com.haoliang.model.vo;

import com.haoliang.model.SysMenu;
import com.haoliang.model.SysUser;
import lombok.Data;


@Data
public class TokenVO {

    /**
     * 身份证认证token信息
     */
    private String token;
    private UserVO2 user;
    /**
     * 角色编码
     */
    private String roleCode;
    /**
     * 用户拥有的菜单
     */
    private java.util.List<SysMenu> menus;


    public TokenVO(String token, SysUser sysUser, String roleCode, java.util.List<SysMenu> menus) {
        this.token = token;
        this.user = new UserVO2(sysUser);
        this.roleCode = roleCode;
        this.menus = menus;
    }


}
