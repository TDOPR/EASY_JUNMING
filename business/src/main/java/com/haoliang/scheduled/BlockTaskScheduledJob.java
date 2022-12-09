package com.haoliang.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.enums.*;
import com.haoliang.model.EvmRecharge;
import com.haoliang.model.EvmWithdraw;
import com.haoliang.service.EvmRechargeService;
import com.haoliang.service.EvmWithdrawService;
import com.haoliang.service.WalletsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dominick Li
 * @Description 区块链任务状态拉取类
 * @CreateTime 2022/11/11 17:48
 **/
@Component
public class BlockTaskScheduledJob {


    @Autowired
    private EvmWithdrawService evmWithdrawService;

    @Autowired
    private EvmRechargeService evmRechargeService;

    @Autowired
    private WalletsService walletsService;

    /**
     * 每隔30秒拉取充值记录表任务状态
     */
    @Scheduled(fixedDelay = 30000)
    @RedisLock
    public void scanRechargeData() {
        //获取充值任务状态是打币成功的
        UpdateWrapper<EvmRecharge> updateWrapper;
        List<EvmRecharge> list = evmRechargeService.list(new LambdaQueryWrapper<EvmRecharge>()
                .select(EvmRecharge::getId, EvmRecharge::getAddress, EvmRecharge::getAmount)
                .eq(EvmRecharge::getStatus, RechargeStatusEnum.RECHARGE_SUCCESS.getStatus()));

        if (list.size() == 0) {
            return;
        }

        //根据区块链地址修改用户钱包金额
        List<Long> idList = new ArrayList<>();
        for (EvmRecharge evmRecharge : list) {
            walletsService.updateWallet(evmRecharge.getAddress(), evmRecharge.getAmount(), FlowingActionEnum.INCOME, FlowingTypeEnum.RECHARGE);
            idList.add(evmRecharge.getId());
        }

        //修改任务状态
        updateWrapper = Wrappers.update();
        updateWrapper.lambda()
                .set(EvmRecharge::getStatus, RechargeStatusEnum.TO_RECORDED_SUCCESS.getStatus())
                .in(EvmRecharge::getId, idList);
        evmRechargeService.update(updateWrapper);
    }


    /**
     * 每隔30秒拉取提现表任务状态
     */
    @Scheduled(fixedDelay = 30000)
    @RedisLock
    public void scanWithdrawData() {
        //获取提现任务状态是打币成功的
        UpdateWrapper<EvmWithdraw> updateWrapper;
        List<EvmWithdraw> list = evmWithdrawService.list(
                new LambdaQueryWrapper<EvmWithdraw>()
                        .select(EvmWithdraw::getId, EvmWithdraw::getUserId, EvmWithdraw::getAmount, EvmWithdraw::getStatus)
                        .eq(EvmWithdraw::getCoinId, CoinUnitEnum.USDT.getId())
                        .in(EvmWithdraw::getStatus, Arrays.asList(WithdrawStatusEnum.SUCCESS.getStatus(), WithdrawStatusEnum.BLOCK_COIN_PRINTING_FAILED.getStatus()))
        );

        if (list.size() == 0) {
            return;
        }

        List<Long> idList = new ArrayList<>();
        for (EvmWithdraw evmWithdraw : list) {
            if (evmWithdraw.getStatus().equals(WithdrawStatusEnum.SUCCESS.getStatus())) {
                walletsService.reduceFrozenAmount(evmWithdraw.getUserId(), evmWithdraw.getAmount());
            } else {
                //如果区块链打币失败 则把冻结的金额返还给客户
                walletsService.unFrozenAmount(evmWithdraw.getUserId(), evmWithdraw.getAmount());
            }
            idList.add(evmWithdraw.getId());
        }

        //修改任务状态为已结算
        updateWrapper = Wrappers.update();
        updateWrapper.lambda()
                .set(EvmWithdraw::getStatus, WithdrawStatusEnum.TO_AMOUNT_SUCCESS.getStatus())
                .in(EvmWithdraw::getId, idList);
        evmWithdrawService.update(updateWrapper);
    }
}
