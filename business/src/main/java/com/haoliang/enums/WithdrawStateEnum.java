package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提现以及审核状态枚举
 */
@AllArgsConstructor
@Getter
public enum WithdrawStateEnum {

    UNDER_REVIEW(0,"待审核"),
    SUCCESS(1,"成功"),
    REJECT(2,"驳回"),
    REVOKE(3,"撤销"),
    CHECK_SUCCESS(4,"审核通过"),
    COIN_STRIKING(5,"打币中"),
    TO_BE_CONFIRMED_BY_THE_BLOCK(6,"待区块确认"),
    BLOCK_COIN_PRINTING_FAILED(7,"区块打币失败"),
    TO_AMOUNT_SUCCESS(8,"已结算"),
    ;

    private Integer state;
    private String desc;

    public static String getDescByState(Integer state){
        for(WithdrawStateEnum withdrawStateEnum:values()){
            if(withdrawStateEnum.getState().equals(state)){
                return withdrawStateEnum.getDesc();
            }
        }
        return "";
    }
}
