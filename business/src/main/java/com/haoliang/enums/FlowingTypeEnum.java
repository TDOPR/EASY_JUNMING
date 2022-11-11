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

    ALGEBRA(1,"代数奖"),
    ROBOT( 2,"推广奖"),
    TEAM(3,"团队奖"),
    LEADER(4,"特别奖"),
    RECHARGE(5,"充值"),
    WITHDRAWAL(6,"提现"),
    STATIC(7,"量化收益"),
    ENTRUSTMENT(8,"委托量化"),
    WITHDRAWL_WALLET(9,"提现到钱包"),
    BUY_ROBOT(10,"首次购买机器人"),
    UPGRADE_ROBOT(11,"升级机器人"),
    ;

    private Integer value;

    private String desc;

    public static String getDescByValue(Integer value){
        for(FlowingTypeEnum flowingTypeEnum: values()){
            if(flowingTypeEnum.getValue().equals(value)){
                return flowingTypeEnum.getDesc();
            }
        }
        return "";
    }


}
