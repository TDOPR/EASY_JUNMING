package com.haoliang.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 14:47
 **/
@Data
public class WalletsVO {

    /**
     * 直推地址数量
     */
    private Integer addressCount;

    /**
     * 直推业绩数量
     */
    private Integer performance;

    /**
     * 总地址数
     */
    private String totalAddressCount;

    /**
     * 总业绩数 (托管本金 + 机器人购买金额)
     */
    private BigDecimal totalPerformance;

    /**
     * 可用余额
     */
    private BigDecimal walletAmount;

    /**
     * 托管本金
     */
    private BigDecimal principalAmount;


}
