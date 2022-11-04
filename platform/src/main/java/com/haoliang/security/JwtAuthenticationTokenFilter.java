package com.haoliang.security;

import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.common.utils.RedisUtils;
import com.haoliang.config.DictionaryParam;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Dominick Li
 * @description toekn认证
 **/
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String token = request.getHeader(JwtTokenUtils.TOKEN_NAME);
        //当前请求中包含令牌
        if (!StringUtils.isEmpty(token)) {
            Claims claims = JwtTokenUtils.getTokenClaim(token);
            if (claims != null) {
                if (!JwtTokenUtils.isTokenExpired(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    boolean flag = true;
                    if (DictionaryParam.isEnableSso()) {
                        //如果有权限则是系统用户
                        if (claims.containsKey("authorities")) {
                            flag = token.equals(RedisUtils.getCacheObject(CacheKeyPrefixConstants.TOKEN + claims.getSubject()));
                        } else {
                            flag = token.equals(RedisUtils.getCacheObject(CacheKeyPrefixConstants.APP_USER_TOKEN + claims.getSubject()));
                        }
                    }
                    //如果是单点登录需要判断当前token和redis里的token是否一致
                    if (flag || !DictionaryParam.isEnableSso()) {
                        UserDetails userDetails = new MyUserDetail(claims);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        logger.debug("authorication user: " + claims.getSubject() + ", setting security context");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        //用户在别的地方已登录 已失效
                        logger.info("token in expire");
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}
