package com.haoliang.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.common.utils.ErrorLogUtil;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.model.DayRate;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.Wallets;
import com.haoliang.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author Dominick Li
 * @Description 日收益
 * @CreateTime 2022/11/1 17:13
 **/
@Component
@Slf4j
public class ProfitTaskScheduledJob {

    @Autowired
    private WalletsService walletsService;

    @Autowired
    private WalletLogsService walletLogsService;

    @Autowired
    private AsyncService asyncService;

    @Resource
    private ProfitLogsService profitLogsService;

    @Autowired
    private DayRateService dayRateService;

    /**
     * 每天晚上21点计算托管收益
     */
    @Scheduled(cron = "0 0 21 * * ?")
    //@Scheduled(fixedDelay = 30000)//测试每10秒执行一次
    @RedisLock
    public void calculationDayProfit() {
        log.info("-------------计算日收益任务开始--------------");
        //查询托管金额大于0的用户钱包信息
        List<Wallets> walletsList = walletsService.list(new LambdaQueryWrapper<Wallets>().ge(Wallets::getPrincipalAmount, 0));
        List<ProfitLogs> profitLogsList = new ArrayList<>(walletsList.size());
        ProfitLogs profitLogs;
        HashMap<Integer, BigDecimal> userAmount = new HashMap<>();
        BigDecimal amount;
        DayRate dayRate = dayRateService.selectNewDayRate();
        for (Wallets wallets : walletsList) {
            amount = wallets.getPrincipalAmount().multiply(dayRateService.getDayRateByLevel(dayRate, wallets.getRobotLevel()));
            userAmount.put(wallets.getUserId(), amount);
            profitLogs = ProfitLogs.builder()
                    .userId(wallets.getUserId())
                    .principal(wallets.getPrincipalAmount())
                    .generatedAmount(amount)
                    .build();
            profitLogsList.add(profitLogs);
        }
        //批量插入日收益日志
        profitLogsService.saveBatch(profitLogsList);
        log.info("-------------计算日收益任务结束--------------");

        //只有托管金额>=300的有资格获取动态收益
        List<Wallets> sendTeamWallets = walletsList.stream().filter(wallets -> wallets.getPrincipalAmount().compareTo(EasyTradeConfig.PROXY_MIN_MONEY) >= 0).collect(Collectors.toList());
        if (sendTeamWallets.size() == 0) {
            return;
        }

        log.info("-------------发放动态收益开始--------------");
        CountDownLatch countDownLatch = new CountDownLatch(sendTeamWallets.size());
        for (Wallets wallets : sendTeamWallets) {
            asyncService.grantItemPrizeToUser(wallets, userAmount, countDownLatch);
        }

        try {
            //主线程挂起,等待所有发放动态收益的子线程处理结束
            countDownLatch.await();
        } catch (Exception e) {
            ErrorLogUtil.save(ProfitTaskScheduledJob.class,"","calculationDayProfit task countDownLatch.await error");
        }
        log.info("-------------发放动态收益结束--------------");
    }

    /**
     * 每周日晚上11点发放周收益给用户
     */
    @Scheduled(cron = "0 0 23 ? * 7")
    //@Scheduled(fixedDelay = 10000) //测试10秒执行一次
    @RedisLock
    @Transactional(rollbackFor = Exception.class)
    public void grantToUser() {
        log.info("-------------发放周收益给用户任务开始--------------");
        List<Wallets> walletsList = walletsService.list();
        List<ProfitLogs> profitLogsList;
        List<Long> allIdList = new ArrayList<>();
        BigDecimal total;
        List<WalletLogs> walletLogList = new ArrayList<>();
        WalletLogs walletLogs;
        for (Wallets wallets : walletsList) {
            //获取用户未结算的收益信息
            profitLogsList = profitLogsService.list(new LambdaQueryWrapper<ProfitLogs>().eq(ProfitLogs::getStatus, 0).eq(ProfitLogs::getUserId, wallets.getUserId()));
            allIdList.addAll(profitLogsList.stream().map(ProfitLogs::getId).collect(Collectors.toList()));
            //获取周收益金额
            total = new BigDecimal("0.0");
            for (ProfitLogs profitLogs : profitLogsList) {
                total = total.add(profitLogs.getGeneratedAmount());
            }
            walletLogs = WalletLogs.builder()
                    .userId(wallets.getUserId())
                    .amount(total)
                    .action(FlowingActionEnum.INCOME.getValue())
                    .type(FlowingTypeEnum.STATIC.getValue())
                    .build();
            //添加钱包交易流水
            walletLogList.add(walletLogs);
        }
        if (allIdList.size() > 0) {
            //修改未结算的流水记录为已结算
            profitLogsService.updateUseByIdList(allIdList);
        }
        //提交钱包静态收益金额变更记录
        walletsService.updateBatchById(walletsList);
        //提交钱包流水日志
        walletLogsService.saveBatch(walletLogList);
        log.info("-------------发放周收益给用户任务结束--------------");
    }


}
