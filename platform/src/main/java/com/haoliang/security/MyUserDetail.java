package com.haoliang.security;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Dominick Li
 * @description 自定义认证用户详细信息
 **/
public class MyUserDetail implements org.springframework.security.core.userdetails.UserDetails {


    private String username;
    private Collection<SimpleGrantedAuthority> authorities;

    public MyUserDetail(Claims claims) {
        this.username = claims.getSubject();
        authorities = new ArrayList<>();
        if (claims.containsKey("authorities")) {
            String authoritiesStr = claims.get("authorities").toString();
            List<String> authoritieList = JSONObject.parseArray(authoritiesStr, String.class);
            for (String str : authoritieList) {
                authorities.add(new SimpleGrantedAuthority(str));
            }
        }
        if (claims.containsKey("roleCode")) {
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class));
            authorities.add(simpleGrantedAuthority);
        }
    }


    /**
     * 获取权限
     */

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /**
     * 帐号是否未过期
     *
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 帐号是否未锁定
     *
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 凭证未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 是否可用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }


}
