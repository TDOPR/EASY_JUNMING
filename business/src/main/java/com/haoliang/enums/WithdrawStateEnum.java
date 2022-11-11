package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum WithdrawStateEnum {

    UNDER_REVIEW(0,"待审核"),
    SUCCESS(1,"成功"),
    CHECK_SUCCESS(2,"审核通过"),
    REJECT(3,"驳回"),
    COIN_STRIKING(5,"打币中"),
    TO_BE_CONFIRMED_BY_THE_BLOCK(6,"待区块确认"),
    BLOCK_COIN_PRINTING_FAILED(7,"区块打币失败"),
    BLOCK_COIN_PRINTING_SUCCESS(7,"区块打币成功"),
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
