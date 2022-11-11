package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum RobotEnum {

    ZERO(0, BigDecimal.ZERO, new BigDecimal(1000)),
    ROBOT_ONE(1, new BigDecimal(100), new BigDecimal(1000)),
    ROBOT_TW0(2, new BigDecimal(200), new BigDecimal(5000)),
    ROBOT_THREE(3, new BigDecimal(300), new BigDecimal(10000)),
    ROBOT_FOUR(4, new BigDecimal(400), BigDecimal.ZERO);

    private int value;
    private BigDecimal price;
    private BigDecimal rechargeMax;


    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getRechargeMax() {
        return rechargeMax;
    }

    public static BigDecimal getRechargeMaxByValue(int value) {
        for (RobotEnum robotEnum : RobotEnum.values()) {
            if (value == robotEnum.getValue()) {
                return robotEnum.getRechargeMax();
            }
        }
        return BigDecimal.ZERO;
    }

    public static RobotEnum valueOf(int value) {
        for (final RobotEnum sys : RobotEnum.values()) {
            if (sys.getValue() == value) {
                return sys;
            }
        }
        return null;
    }
}
