package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.model.WalletLogs;

import java.math.BigDecimal;

public interface WalletLogsService extends IService<WalletLogs> {

    /**
     * 插入流水记录
     * @param amount  变更的金额
     * @param userId  用户Id
     * @param flowingActionEnum 收入或支出
     * @param flowingTypeEnum 流水类型
     * @return 执行结果
     */
    boolean insertWalletLogs(BigDecimal amount, Integer userId, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum);

}
