package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;


/**
 * @author Dominick Li
 * @Description 代理商团队分红枚举
 * @CreateTime 2022/11/1 10:30
 **/
@Getter
@AllArgsConstructor
public enum ProxyLevelEnum {

    ONE(1, new BigDecimal("0.05"), new BigDecimal(10000)),
    TWO(2, new BigDecimal("0.10"), new BigDecimal(30000)),
    THREE(3, new BigDecimal("0.15"), new BigDecimal(100000)),
    FOUR(4, new BigDecimal("0.20"), new BigDecimal(300000)),
    FIVE(5, new BigDecimal("0.30"), new BigDecimal(1000000)),
    ;

    /**
     * 代理商等级
     */
    private Integer level;

    /**
     * 收益比
     */
    private BigDecimal IncomeRatio;

    /**
     * 量化金额
     */
    private BigDecimal money;

    public static ProxyLevelEnum getByLevel(Integer level) {
        for (ProxyLevelEnum proxyLevelEnum : values()) {
            if (proxyLevelEnum.getLevel().equals(level)) {
                return proxyLevelEnum;
            }
        }
        return null;
    }


    /**
     * 根据业绩范围获取代理商等级对象
     * @param amount 业绩
     * @return  代理商等级对象
     */
    public static ProxyLevelEnum valueOfByAmount(BigDecimal amount) {
        if (amount.compareTo(ONE.money) < 0) {
            return null;
        } else if (amount.compareTo(TWO.money) < 0) {
            return ONE;
        } else if (amount.compareTo(THREE.money) < 0) {
            return TWO;
        } else if (amount.compareTo(FOUR.money) < 0) {
            return THREE;
        } else if (amount.compareTo(FIVE.money) < 0) {
            return FOUR;
        }
        return FIVE;
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal("30000").compareTo(ProxyLevelEnum.ONE.money));
    }
}
