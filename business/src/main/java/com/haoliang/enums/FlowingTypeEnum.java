package com.haoliang.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Dominick Li
 * @Description 钱包流水类型枚举
 * @CreateTime 2022/11/1 10:30
 **/
@Getter
@AllArgsConstructor
public enum FlowingTypeEnum {

    ALGEBRA(1, "代数奖励"),
    ROBOT(2, "推广奖励"),
    TEAM(3, "团队奖励"),
    SPECIAL(4, "分红奖励"),
    RECHARGE(5, "充值"),
    WITHDRAWAL(6, "提现"),
    STATIC(7, "量化收益"),
    ENTRUSTMENT(8, "量化投入"),
    WITHDRAWL_WALLET(9, "量化取出"),
    BUY_ROBOT(10, "购买机器人"),
    UPGRADE_ROBOT(11, "升级机器人"),
    ;

    private Integer value;

    private String desc;

    public static String getDescByValue(Integer value) {
        for (FlowingTypeEnum flowingTypeEnum : values()) {
            if (flowingTypeEnum.getValue().equals(value)) {
                return flowingTypeEnum.getDesc();
            }
        }
        return "";
    }


    public static String getWalletDescByValue(Integer value) {
        if (value.equals(RECHARGE.value)) {
            return RECHARGE.desc;
        } else if (value.equals(WITHDRAWAL.value)) {
            return WITHDRAWAL.desc;
        } else if (value.equals(ENTRUSTMENT.value)) {
            return ENTRUSTMENT.desc;
        } else if (value.equals(WITHDRAWL_WALLET.value)) {
            return WITHDRAWL_WALLET.desc;
        } else if (value.equals(STATIC.value)) {
            return "量化收益存入";
        } else if (value.equals(BUY_ROBOT.value) || value.equals(UPGRADE_ROBOT.value)) {
            return BUY_ROBOT.desc;
        }
        return "";
    }

}
