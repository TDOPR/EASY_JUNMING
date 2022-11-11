package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description 提现的货币类型
 * @CreateTime 2022/11/1 16:01
 **/
@Getter
@AllArgsConstructor
public enum CoinUnitEnum {

    LEGAL_CURRENCY(1, "法币", new BigDecimal(0.03)),
    USDT(2, "Usdt数字货币", new BigDecimal(0.01));

    /**
     * 类型id
     */
    private Integer type;

    /**
     * 货币名称
     */
    private String desc;

    /**
     * 提现手续费
     */
    private BigDecimal interestRate;


    public static CoinUnitEnum valueOfByType(Integer type) {
        for (CoinUnitEnum coinUnitEnum : values()) {
            if (type.equals(coinUnitEnum.getType())) {
                return coinUnitEnum;
            }
        }
        return null;
    }
    
    public static String getDescByType(Integer type){
        for (CoinUnitEnum coinUnitEnum : values()) {
            if (type.equals(coinUnitEnum.getType())) {
                return coinUnitEnum.getDesc();
            }
        }
        return "";
    }
}
