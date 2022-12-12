package com.haoliang.enums;


import com.haoliang.common.constant.LanguageKeyConstants;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.util.MessageUtil;

/**
 * @author Dominick Li
 * @Description 钱包流水类型枚举
 * @CreateTime 2022/11/1 10:30
 **/
public enum FlowingTypeEnum {

    ALGEBRA(1, "代数奖励"),
    ROBOT(2, "AI奖励"),
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

    private String name;

    private String key;

    public Integer getValue() {
        return value;
    }

    /**
     * 国际化信息文件里的Key前缀
     */
    private final static String prefix = "flowingType.";

    FlowingTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
        this.key = prefix + value;
    }


    public static String getDescByValue(Integer value) {
        for (FlowingTypeEnum flowingTypeEnum : values()) {
            if (flowingTypeEnum.getValue().equals(value)) {
                return flowingTypeEnum.toString();
            }
        }
        return "";
    }

    public static FlowingTypeEnum valueOf(Integer value) {
        for (FlowingTypeEnum flowingTypeEnum : values()) {
            if (flowingTypeEnum.getValue().equals(value)) {
                return flowingTypeEnum;
            }
        }
        return null;
    }


    public static String getWalletDescByValue(Integer value) {
        if (value.equals(RECHARGE.value)) {
            return RECHARGE.toString();
        } else if (value.equals(WITHDRAWAL.value)) {
            return WITHDRAWAL.toString();
        } else if (value.equals(ENTRUSTMENT.value)) {
            return ENTRUSTMENT.toString();
        } else if (value.equals(WITHDRAWL_WALLET.value)) {
            return WITHDRAWL_WALLET.toString();
        } else if (value.equals(STATIC.value)) {
            return MessageUtil.get(LanguageKeyConstants.FLOWING_TYPE_STATIC, ThreadLocalManager.getLanguage());
        } else if (value.equals(BUY_ROBOT.value) || value.equals(UPGRADE_ROBOT.value)) {
            return BUY_ROBOT.toString();
        }
        return "";
    }

    @Override
    public String toString() {
        return MessageUtil.get(key, ThreadLocalManager.getLanguage());
    }
}
