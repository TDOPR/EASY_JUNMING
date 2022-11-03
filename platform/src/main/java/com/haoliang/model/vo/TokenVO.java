package com.haoliang.model.vo;

import lombok.Data;


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
     * 用户拥有的菜单权限
     */
    private RouterVO routerAuthority;


    public TokenVO(String token, String roleCode, RouterVO routerAuthority) {
        this.token = token;
        this.roleCode = roleCode;
        this.routerAuthority = routerAuthority;
    }


}
