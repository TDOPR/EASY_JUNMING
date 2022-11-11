package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.mapper.WalletLogsMapper;
import com.haoliang.model.WalletLogs;
import com.haoliang.service.WalletLogsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 18:54
 **/
@Service
public class WalletLogsServiceImpl extends ServiceImpl<WalletLogsMapper, WalletLogs> implements WalletLogsService {

    @Resource
    WalletLogsMapper walletLogsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertWalletLogs(BigDecimal amount, Integer userId, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum) {
        //添加钱包流水记录
        WalletLogs walletLogs = WalletLogs.builder()
                .userId(userId)
                .amount(amount)
                .action(flowingActionEnum.getValue())
                .type(flowingTypeEnum.getValue())
                .build();
        walletLogsMapper.insert(walletLogs);
        return false;
    }
}
