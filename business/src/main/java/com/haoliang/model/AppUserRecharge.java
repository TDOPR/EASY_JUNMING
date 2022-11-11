package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 充值记录
 */
@Data
@Builder
@TableName("app_user_recharge")
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRecharge extends BaseModel {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 交易id
     */
    private String txid;

    /**
     * 货币类型 1=法币 2=usdt
     */
    private Integer coinUnit;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 充值状态 1=成功 5-打币中;6;-待区块确认;7-区块打币失败
     */
    private Integer status;

    /**
     * 区块链充值地址
     */
    private String address;

    /**
     * 充值币种和美元的汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 换算成美元后的金额
     */
    private BigDecimal usdAmount;

}
