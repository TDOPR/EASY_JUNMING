package com.haoliang.enums;

import java.math.BigDecimal;

public enum RobotEnum {

    ROBOT_ONE(1, new BigDecimal("100"),new BigDecimal("1000")),
    ROBOT_TW0(2, new BigDecimal("200"),new BigDecimal("5000")),
    ROBOT_THREE(3, new BigDecimal("300"),new BigDecimal("10000")),
    ROBOT_FOUR(4, new BigDecimal("400"),BigDecimal.ONE);

    private int code;
    private BigDecimal price;
    private BigDecimal rechargeMax;

    RobotEnum(int value, BigDecimal price,BigDecimal rechargeMax) {
        this.code = value;
        this.price = price;
        this.rechargeMax = rechargeMax;
    }

    public int getCode() {
        return code;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getRechargeMax() {
        return rechargeMax;
    }

    public static BigDecimal getRechargeMaxByCode(int code) {
        for (RobotEnum robotEnum : RobotEnum.values()) {
            if (code == robotEnum.getCode()) {
                return robotEnum.getRechargeMax();
            }
        }
        return BigDecimal.ZERO;
    }

    public static RobotEnum parseValue(int value) {

        for (final RobotEnum sys : RobotEnum.values()) {
            if (sys.getCode() == value) {
                return sys;
            }
        }
        return null;
    }
}
