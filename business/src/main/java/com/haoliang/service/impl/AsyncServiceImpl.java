package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.AlgebraEnum;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.ProxyLevelEnum;
import com.haoliang.model.TreePath;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.MyItemAmountDTO;
import com.haoliang.service.AsyncService;
import com.haoliang.service.TreePathService;
import com.haoliang.service.WalletLogsService;
import com.haoliang.service.WalletsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author Dominick Li
 * @Description 异步任务处理
 * @CreateTime 2022/11/14 14:41
 **/
@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService {

    @Autowired
    private TreePathService treePathService;

    @Autowired
    private WalletLogsService walletLogsService;

    @Autowired
    private WalletsService walletsService;

    @Override
    @Async("asyncExecutor")
    public void grantItemPrizeToUser(Wallets wallets, HashMap<Integer, BigDecimal> userAmount, CountDownLatch countDownLatch) {
        try {
            List<TreePath> treePathList = treePathService.getThreeAlgebraTreePathByUserId(wallets.getUserId());
            boolean flag = false;
            //*****************************发放代数奖*************************************
            {
                //总收益
                BigDecimal totalIncome = new BigDecimal("0");
                BigDecimal userIncome;
                for (TreePath treePath : treePathList) {
                    userIncome = userAmount.get(treePath.getDescendant());
                    if (userIncome != null) {
                        totalIncome = totalIncome.add(userIncome.multiply(AlgebraEnum.getRechargeMaxByLevel(treePath.getLevel())));
                    }
                }
                if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
                    flag = true;
                    //如果代数奖收益大于0,则发放代数奖奖励给用户
                    wallets.setWalletAmount(wallets.getWalletAmount().add(totalIncome));
                    //插入流水变更记录
                    walletLogsService.insertWalletLogs(totalIncome, wallets.getUserId(), FlowingActionEnum.INCOME, FlowingTypeEnum.ALGEBRA);
                    log.info("发放代数奖: userId={} ,amount={} ", wallets.getUserId(), totalIncome);
                }
            }

            //*****************************发放团队奖和领导奖*************************************
            {
                List<TreePath> firstTreePath = treePathList.stream().filter(treePath -> treePath.getLevel() == 1).collect(Collectors.toList());
                //需要团队数量大于1的才有资格发放团队奖和特别奖
                if (firstTreePath.size() > 1) {
                    //获取我的团队收益情况
                    MyItemAmountDTO myItemAmountDTO = walletsService.getMyItemAmount(firstTreePath.stream().map(TreePath::getDescendant).collect(Collectors.toList()));

                    ProxyLevelEnum proxyLevelEnum = ProxyLevelEnum.valueOfByAmount(myItemAmountDTO.getMinItemAmount());
                    if (proxyLevelEnum == null) {
                        //业绩未达标不发放金额
                        log.info("团队业绩未达标: userId={},amount={}", wallets.getUserId(), myItemAmountDTO.getMinItemAmount());
                    } else {
                        flag = true;
                        //计算小团队的量化收益总量
                        BigDecimal minProfit = BigDecimal.ZERO, amount;
                        for (Integer userId : myItemAmountDTO.getMinItemUserIdList()) {
                            amount = userAmount.get(userId);
                            if (amount != null) {
                                minProfit = minProfit.add(amount);
                            }
                        }

                        //根据代理商等级对应的收益比计算发放给客户的团队奖  =小团队的量化收益 * 收益率
                        BigDecimal sendAmount = minProfit.multiply(proxyLevelEnum.getIncomeRatio());
                        wallets.setWalletAmount(wallets.getWalletAmount().add(sendAmount));
                        walletLogsService.insertWalletLogs(sendAmount, wallets.getUserId(), FlowingActionEnum.INCOME, FlowingTypeEnum.TEAM);
                        log.info("发放团队奖: userId={} ,amount={} ", wallets.getUserId(), sendAmount);

                        //判断代理商等级是否有资格发放特别奖  需要级别在4以上才能分红
                        if (proxyLevelEnum.getLevel() >= ProxyLevelEnum.FOUR.getLevel()) {
                            BigDecimal allProfit = minProfit;
                            //统计大团队的收益
                            for (Integer userId : myItemAmountDTO.getMaxItemUserIdList()) {
                                amount = userAmount.get(userId);
                                if (amount != null) {
                                    allProfit = allProfit.add(amount);
                                }
                            }
                            sendAmount = allProfit.multiply(EasyTradeConfig.SPECIAL_AWARD_RATE);
                            wallets.setWalletAmount(wallets.getWalletAmount().add(sendAmount));
                            walletLogsService.insertWalletLogs(sendAmount, wallets.getUserId(), FlowingActionEnum.INCOME, FlowingTypeEnum.SPECIAL);
                            log.info("发放领导奖奖: userId={} ,amount={} ", wallets.getUserId(), sendAmount);
                        }
                    }

                }
            }

            if (flag) {
                //金额变更修改钱包余额
                UpdateWrapper<Wallets> walletsUpdateWrapper = Wrappers.update();
                walletsUpdateWrapper.lambda().
                        set(Wallets::getWalletAmount, wallets.getWalletAmount())
                        .eq(Wallets::getId, wallets.getId());
                walletsService.update(walletsUpdateWrapper);
            }
        } finally {
            countDownLatch.countDown();
        }
    }


}
