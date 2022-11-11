package com.haoliang.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.WithdrawStateEnum;
import com.haoliang.model.AppUserRecharge;
import com.haoliang.model.AppUserWithdraw;
import com.haoliang.service.AppUserRechargeService;
import com.haoliang.service.AppUserWithdrawService;
import com.haoliang.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominick Li
 * @Description 区块链任务状态拉取类
 * @CreateTime 2022/11/11 17:48
 **/
@Component
public class BlockTaskScheduledJob {


    @Autowired
    private AppUserWithdrawService appUserWithdrawService;

    @Autowired
    private AppUserRechargeService appUserRechargeService;

    @Autowired
    private WalletService walletService;

    /**
     * 每隔30秒拉取充值记录表任务状态
     */
    @Scheduled(fixedDelay = 30000)
    @RedisLock
    public void scanRechargeData() {
        //获取充值任务状态是打币成功的
        UpdateWrapper<AppUserRecharge> updateWrapper;
        List<AppUserRecharge> list = appUserRechargeService.list(new LambdaQueryWrapper<AppUserRecharge>()
                .select(AppUserRecharge::getId, AppUserRecharge::getUsdAmount)
                .eq(AppUserRecharge::getStatus, WithdrawStateEnum.BLOCK_COIN_PRINTING_SUCCESS.getState()));

        if (list.size() == 0) {
            return;
        }

        List<Long> idList = new ArrayList<>();
        for (AppUserRecharge appUserRecharge : list) {
            walletService.updateWallet(appUserRecharge.getUsdAmount(), appUserRecharge.getUserId(), FlowingActionEnum.INCOME, FlowingTypeEnum.RECHARGE);
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
     * 每隔30秒拉取充值提现表任务状态
     */
    @Scheduled(fixedDelay = 30000)
    @RedisLock
    public void scanWithdrawData() {
        //获取充值任务状态是打币成功的
        UpdateWrapper<AppUserWithdraw> updateWrapper;
        List<AppUserWithdraw> list = appUserWithdrawService.list(new LambdaQueryWrapper<AppUserWithdraw>()
                .select(AppUserWithdraw::getId, AppUserWithdraw::getAmount)
                .eq(AppUserWithdraw::getStatus, WithdrawStateEnum.BLOCK_COIN_PRINTING_SUCCESS.getState()));

        if (list.size() == 0) {
            return;
        }

        List<Long> idList = new ArrayList<>();
        for (AppUserWithdraw appUserWithdraw : list) {
            walletService.updateWallet(appUserWithdraw.getAmount(), appUserWithdraw.getUserId(), FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.WITHDRAWAL);
            idList.add(appUserWithdraw.getId());
        }

        //修改任务状态
        updateWrapper = Wrappers.update();
        updateWrapper.lambda()
                .set(AppUserWithdraw::getStatus, WithdrawStateEnum.SUCCESS.getState())
                .in(AppUserWithdraw::getId, idList);
        appUserWithdrawService.update(updateWrapper);
    }
}
