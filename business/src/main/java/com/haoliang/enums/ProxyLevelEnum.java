package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author Dominick Li
 * @Description 代理商团队分红枚举
 * @CreateTime 2022/11/1 10:30
 **/
@Getter
@AllArgsConstructor
public enum ProxyLevelEnum {

    ONE(1, 5, 10000),
    TWO(2, 10, 30000),
    THREE(3, 15, 100000),
    FOUR(4, 20, 300000),
    FIVE(5, 25, 1000000),
    ;

    /**
     * 代理商等级
     */
    private Integer level;

    /**
     * 收益比
     */
    private Integer IncomeRatio;

    /**
     * 量化金额
     */
    private Integer money;

    public static ProxyLevelEnum getByLevel(Integer level) {
        for (ProxyLevelEnum proxyLevelEnum : values()) {
            if (proxyLevelEnum.getLevel().equals(level)) {
                return proxyLevelEnum;
            }
        }
        return null;
    }

}
