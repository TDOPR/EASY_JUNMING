package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description 机器人推广奖枚举 奖励比例
 * @CreateTime 2022/11/10 18:02
 **/
@Getter
@AllArgsConstructor
public enum ProxyRebotEnum {

    FIRST(1, new BigDecimal("0.10")),
    SECOND(2, new BigDecimal("0.15")),
    THIRD(3, new BigDecimal("0.20")),
    FOURTH(4, new BigDecimal("0.25")),
    FIFTH(5, new BigDecimal("0.30")),
    ;

    /**
     * 位数
     */
    private Integer level;

    /**
     * 奖励比例
     */
    private BigDecimal rewardProportion;

    /**
     * 根据位数获取奖励比例,当超过五位的时候拿最大值
     * @param level 位数
     * @return  奖励比例
     */
    public static BigDecimal getRewardProportionByLevel(Integer level) {
        for (ProxyRebotEnum proxyRebotEnum : values()) {
            if (proxyRebotEnum.level.equals(level)) {
                return proxyRebotEnum.getRewardProportion();
            }
        }
        return FIFTH.getRewardProportion();
    }
}
