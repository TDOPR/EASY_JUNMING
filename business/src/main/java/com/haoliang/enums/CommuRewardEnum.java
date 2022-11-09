package com.haoliang.enums;

import java.math.BigDecimal;

public enum CommuRewardEnum {

    REWARD_RATE_ZERO(0, new BigDecimal("0"), new BigDecimal("0")),
    REWARD_RATE_ONE(1, new BigDecimal("5"), new BigDecimal("10000")),
    REWARD_RATE_TW0(2, new BigDecimal("10"), new BigDecimal("30000")),
    REWARD_RATE_THREE(3, new BigDecimal("15"), new BigDecimal("100000")),
    REWARD_RATE_FOUR(4, new BigDecimal("20"), new BigDecimal("300000")),
    REWARD_RATE_FIVE(5, new BigDecimal("25"), new BigDecimal("1000000"));

    private int level;
    private BigDecimal  rate;

    private BigDecimal value;



    CommuRewardEnum(int level, BigDecimal rate, BigDecimal value) {
        this.rate = rate;
        this.level = level;
        this.value = value;
    }

//    public int getCode() {
//        return code;
//    }


    public BigDecimal getRate() {
        return rate;
    }

    public int getLevel() {
        return level;
    }

    public BigDecimal getValue() {return value;}

    public static BigDecimal getRateByLevel(int level) {
        for (CommuRewardEnum commuRewardEnum : CommuRewardEnum.values()) {
            if (level == commuRewardEnum.getLevel()) {
                return commuRewardEnum.getRate();
            }
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal getValueByLevel(int level) {
        for (CommuRewardEnum commuRewardEnum : CommuRewardEnum.values()) {
            if (level == commuRewardEnum.getLevel()) {
                return commuRewardEnum.getValue();
            }
        }
        return BigDecimal.ZERO;
    }

}
