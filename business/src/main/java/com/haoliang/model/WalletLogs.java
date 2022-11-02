package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelNoModifyTime;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description 用户钱包
 * @CreateTime 2022/11/1 10:57
 **/
@Data
@TableName("wallet_logs")
@NoArgsConstructor
public class WalletLogs extends BaseModelNoModifyTime {

    public WalletLogs(Integer userId, Integer targetUserId, BigDecimal amount, Integer action, Integer type) {
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.amount = amount;
        this.action = action;
        this.type = type;
    }

    /**
     * 所属用户Id
     */
    private Integer userId;

    /**
     * 目标用户ID
     */
    private Integer targetUserId;

    /**
     * 本次变动金额
     */
    private BigDecimal amount;

    /**
     * 收支类型 1=收入 2=支出
     */
    private Integer action;


    /**
     * 流水类型  1-ai奖励(机器人)  2-社区推广奖励 3-社区团队奖励 4-特级分红奖励 5-提现违约金
     */
    private Integer type;



}
