package com.haoliang.common.utils;

import com.haoliang.common.config.GlobalConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author Dominick Li
 * @description java web token 配置类
 **/
@Slf4j
public class JwtTokenUtils {

    public static final String TOKEN_NAME="token";

    private static final Clock clock = DefaultClock.INSTANCE;

    /**
     * 根据身份ID标识，生成Token
     */
    public static String getToken(Integer identityId, String authorities,String username) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + GlobalConfig.getTokenExpire() * 1000);
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                //放入唯一标识,可以是用户名或者Id
                .setSubject(identityId.toString())
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, GlobalConfig.getTokenSecret())
                //自定义属性 放入用户拥有请求权限
                .claim("authorities", authorities)
                .claim("userName", username)
                .compact();
    }

    /**
     * 根据身份ID标识，生成Token
     */
    public static String getToken(Integer identityId) {
        Date nowDate = new Date();
        //过期时间
        Date expireDate = new Date(nowDate.getTime() + GlobalConfig.getTokenExpire() * 1000);
        return Jwts.builder()
                .setHeaderParam("type", "JWT")
                //放入唯一标识,可以是用户名或者Id
                .setSubject(identityId.toString())
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, GlobalConfig.getTokenSecret())
                .compact();
    }

    /**
     * 根据token获取身份信息
     */
    public static Claims getTokenClaim(String token) {
        try {
            return Jwts.parser().setSigningKey(GlobalConfig.getTokenSecret()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.error("getTokenClaim error:{}", e.getMessage());
            return null;
        }
    }

    /**
     * 判断token是否失效
     */
    public static boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration.before(clock.now());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 根据token获取username
     */
    public static Integer getUserIdFromToken(String token) {
        return Integer.parseInt(getClaimFromToken(token, Claims::getSubject));
    }

    /**
     * 根据token获取角色
     */
    public static String getRoleCodeFromToken(String token) {
        return getTokenClaim(token).get("role").toString().toLowerCase();
    }

    /**
     * 根据token获取用户名
     */
    public static String getUserNameFromToken(String token) {
        return getTokenClaim(token).get("userName").toString();
    }

    /**
     * 根据token获取失效时间
     */
    public static Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public static <T> T getClaimFromToken(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = getTokenClaim(token);
        return claimsResolver.apply(claims);
    }


}
