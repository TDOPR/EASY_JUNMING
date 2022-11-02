package com.haoliang.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Dominick Li
 * @Description 流水类型枚举
 * @CreateTime 2022/11/1 10:30
 **/
@Getter
@AllArgsConstructor
public enum FlowingTypeEnum {

    AI( 1,"AI直推奖励"),
    TEAM(3,"团队奖励"),
    WITHDRAW(5,"钱包提现"),
    WEEK(6,"周结算收益"),
    ;

    private Integer value;

    private String name;

}
