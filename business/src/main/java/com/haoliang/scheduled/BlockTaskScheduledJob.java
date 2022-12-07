package com.haoliang.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.enums.CoinUnitEnum;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.WithdrawStateEnum;
import com.haoliang.model.AppUserRecharge;
import com.haoliang.model.EvmWithdraw;
import com.haoliang.service.AppUserRechargeService;
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
    private AppUserRechargeService appUserRechargeService;

    @Autowired
    private WalletsService walletsService;

    /**
     * 每隔30秒拉取充值记录表任务状态
     */
    @Scheduled(fixedDelay = 30000)
    @RedisLock
    public void scanRechargeData() {
        //获取充值任务状态是打币成功的
        UpdateWrapper<AppUserRecharge> updateWrapper;
        List<AppUserRecharge> list = appUserRechargeService.list(new LambdaQueryWrapper<AppUserRecharge>()
                .select(AppUserRecharge::getId, AppUserRecharge::getAddress, AppUserRecharge::getUsdAmount));
                //.eq(AppUserRecharge::getStatus, WithdrawStateEnum.BLOCK_COIN_PRINTING_FAILED.getState()));

        if (list.size() == 0) {
            return;
        }

        //根据区块链地址修改用户钱包金额
        List<Long> idList = new ArrayList<>();
        for (AppUserRecharge appUserRecharge : list) {
            walletsService.updateWallet(appUserRecharge.getUsdAmount(), appUserRecharge.getAddress(), FlowingActionEnum.INCOME, FlowingTypeEnum.RECHARGE);
            idList.add(appUserRecharge.getId());
        }

        //修改任务状态
        updateWrapper = Wrappers.update();
        updateWrapper.lambda()
                .set(AppUserRecharge::getStatus, WithdrawStateEnum.SUCCESS.getState())
                .in(AppUserRecharge::getId, idList);
        appUserRechargeService.update(updateWrapper);
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
                        .in(EvmWithdraw::getStatus, Arrays.asList(WithdrawStateEnum.SUCCESS.getState(), WithdrawStateEnum.BLOCK_COIN_PRINTING_FAILED.getState()))
        );

        if (list.size() == 0) {
            return;
        }

        List<Long> idList = new ArrayList<>();
        for (EvmWithdraw evmWithdraw : list) {
            if (evmWithdraw.getStatus().equals(WithdrawStateEnum.SUCCESS.getState())) {
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
                .set(EvmWithdraw::getStatus, WithdrawStateEnum.TO_AMOUNT_SUCCESS.getState())
                .in(EvmWithdraw::getId, idList);
        evmWithdrawService.update(updateWrapper);
    }
}
