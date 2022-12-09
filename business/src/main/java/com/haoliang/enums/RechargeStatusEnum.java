package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 充值状态枚举
 */
@AllArgsConstructor
@Getter
public enum RechargeStatusEnum {

    TO_BE_RECORDED(0,"待入账"),
    RECHARGE_SUCCESS(1,"充值成功"),
    TO_RECORDED_FAILED(2,"到账失败"),
    TO_RECORDED_SUCCESS(3,"到账成功"),
    ;

    private Integer status;
    private String desc;

    public static String getDescByStatus(Integer status){
        for(RechargeStatusEnum rechargeStatusEnum :values()){
            if(rechargeStatusEnum.getStatus().equals(status)){
                return rechargeStatusEnum.getDesc();
            }
        }
        return "";
    }

}
