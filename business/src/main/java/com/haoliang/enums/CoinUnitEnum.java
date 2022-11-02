package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Dominick Li
 * @Description 提现的货币类型
 * @CreateTime 2022/11/1 16:01
 **/
@Getter
@AllArgsConstructor
public enum  CoinUnitEnum {

    LEGAL_CURRENCY(1,"法币",0.02d),
    USDT(2,"Usdt数字货币",0.01d);

    /**
     * 类型id
     */
    private Integer value;

    /**
     * 货币名称
     */
    private String coinUnitName;

    /**
     * 提现手续费
     */
    private Double interestRate;

}
