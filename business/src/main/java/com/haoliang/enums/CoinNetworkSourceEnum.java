package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description 数字货币网络来源
 * @CreateTime 2022/12/6 16:25
 **/
@AllArgsConstructor
@Getter
public enum CoinNetworkSourceEnum {

    BSC("BSC", new BigDecimal("0.02"), new BigDecimal("1")),
    ETH("ETH", new BigDecimal("0.05"), new BigDecimal("3")),
    TRC("TRC", new BigDecimal("0.02"), new BigDecimal("1"));

    private String name;

    /**
     * 提现手续费
     */
    private BigDecimal free;

    /**
     * 提现最低金额要求
     */
    private BigDecimal minAmount;

    /**
     * 根据充值网络名称获取对应的手续费
     *
     * @param name
     * @return
     */
    public static BigDecimal getFreeByName(String name) {
        for (CoinNetworkSourceEnum coinNetworkSourceEnum : values()) {
            if (coinNetworkSourceEnum.getName().equals(name)) {
                return coinNetworkSourceEnum.getFree();
            }
        }
        return BigDecimal.ZERO;
    }

    public static CoinNetworkSourceEnum nameOf(String name) {
        for (CoinNetworkSourceEnum coinNetworkSourceEnum : values()) {
            if (coinNetworkSourceEnum.getName().equals(name)) {
                return coinNetworkSourceEnum;
            }
        }
        return null;
    }
}
