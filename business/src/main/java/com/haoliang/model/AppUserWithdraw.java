package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户提现记录表
 */
@Data
@Builder
@TableName("app_user_withdraw")
@NoArgsConstructor
@AllArgsConstructor
public class AppUserWithdraw extends BaseModel {

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 交易id
     */
    private String txid;

    /**
     * 币种类型
     */
    private Integer coinUnit;

    /**
     * 提现金额(包含手续费)
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 实际提现金额
     */
    private BigDecimal actualAmount;

    /**
     * 状态：
     * 0-待审核;1-成功;5-打币中;6;-待区块确认;7-区块打币失败
     */
    private Integer status;

    /**
     * 区块链地址
     */
    private String address;

    /**
     * 链上手续费花费
     */
    private BigDecimal chainFee;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核状态4=不需要审核小额提现  0=待审核 2=审核通过 3=驳回
     */
    private Integer auditStatus;

}
