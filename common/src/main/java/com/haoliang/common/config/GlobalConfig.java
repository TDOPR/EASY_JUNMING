package com.haoliang.common.config;

import org.redisson.api.RateType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description 全局静态变量配置
 * @Author Dominick Li
 * @CreateTime 2022/10/20 17:08
 **/
@Component
public class GlobalConfig {

    private static String tokenName;

    private static RateType rateType;

    private static long tokenExpire;

    public static String getTokenName() {
        return tokenName;
    }

    @Value("${jwt.tokenName}")
    public  void setTokenName(String tokenName) {
        GlobalConfig.tokenName = tokenName;
    }

    public static RateType getRateType() {
        return rateType;
    }

    public static void setRateType(RateType rateType) {
        GlobalConfig.rateType = rateType;
    }

    public static long getTokenExpire() {
        return tokenExpire;
    }

    @Value("${jwt.expire}")
    public  void setTokenExpire(long tokenExpire) {
        GlobalConfig.tokenExpire = tokenExpire;
    }
}
