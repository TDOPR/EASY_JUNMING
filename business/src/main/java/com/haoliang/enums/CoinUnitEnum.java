package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description 提现的货币类型  网络来源
 * @CreateTime 2022/11/1 16:01
 **/
@Getter
@AllArgsConstructor
public enum CoinUnitEnum {

    USDT(1, "USDT", BigDecimal.ZERO),
    FIAT(2, "FIAT", new BigDecimal("0.03"));

    /**
     * 类型id
     */
    private Integer Id;

    /**
     * 货币名称
     */
    private String name;

    /**
     * 提现手续费
     */
    private BigDecimal interestRate;


    public static CoinUnitEnum idOf(Integer id) {
        for (CoinUnitEnum coinUnitEnum : values()) {
            if (coinUnitEnum.getId().equals(id)) {
                return coinUnitEnum;
            }
        }
        return null;
    }

    public static String getNameById(Integer id) {
        for (CoinUnitEnum coinUnitEnum : values()) {
            if (coinUnitEnum.getId().equals(id)) {
                return coinUnitEnum.getName();
            }
        }
        return "";
    }
}
