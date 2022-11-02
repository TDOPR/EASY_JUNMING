package com.haoliang.model.vo;

import com.haoliang.model.SysMenu;
import lombok.Data;

import java.util.List;


@Data
public class TokenVO {

    /**
     * 身份证认证token信息
     */
    private String token;

    /**
     * 角色编码
     */
    private String roleCode;
    /**
     * 用户拥有的菜单
     */
    private List<SysMenu> menus;


    public TokenVO(String token, String roleCode, List<SysMenu> menus) {
        this.token = token;
        this.roleCode = roleCode;
        this.menus = menus;
    }


}
