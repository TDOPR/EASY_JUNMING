package com.haoliang.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.Wallets;
import com.haoliang.service.ProfitLogsService;
import com.haoliang.service.WalletLogsService;
import com.haoliang.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dominick Li
 * @Description 收益
 * @CreateTime 2022/11/1 17:13
 **/
@Component
@Slf4j
public class ProfitTaskScheduledJob {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletLogsService walletLogsService;

    @Resource
    private ProfitLogsService profitLogsService;

    /**
     * 每天晚上0点计算托管收益
     */
    @Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(fixedDelay = 5000)//测试5秒执行一次
    @RedisLock
    public void calculationDayProfit() {
        log.info("-------------计算日收益任务开始--------------");
        //查询托管金额大于0的用户钱包信息
        List<Wallets> walletsList = walletService.list(new LambdaQueryWrapper<Wallets>().ge(Wallets::getPrincipalAmount, 0));
        List<ProfitLogs> profitLogsList = new ArrayList<>(walletsList.size());
        for (Wallets wallets : walletsList) {
            profitLogsList.add(new ProfitLogs(wallets.getUserId(), wallets.getPrincipalAmount(), EasyTradeConfig.PROFIT_RATE, wallets.getPrincipalAmount().multiply(EasyTradeConfig.PROFIT_RATE)));
        }
        //批量插入日收益日志
        profitLogsService.saveBatch(profitLogsList);
        log.info("-------------计算日收益任务结束--------------");
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
        List<Wallets> walletsList = walletService.list();
        List<ProfitLogs> profitLogsList;
        List<Long> allIdList = new ArrayList<>();
        BigDecimal total;
        List<WalletLogs> walletLogList = new ArrayList<>();

        for (Wallets wallets : walletsList) {
            //获取用户未结算的收益信息
            profitLogsList = profitLogsService.list(new LambdaQueryWrapper<ProfitLogs>().eq(ProfitLogs::getGrantToUser, 0).eq(ProfitLogs::getUserId, wallets.getUserId()));
            allIdList.addAll(profitLogsList.stream().map(ProfitLogs::getId).collect(Collectors.toList()));
            //获取周收益金额
            total = new BigDecimal("0.0");
            for(ProfitLogs profitLogs: profitLogsList){
                total=total.add(profitLogs.getGeneratedAmount());
            }
            //修改静态收益总数
            wallets.setStaticRewardAmount(wallets.getStaticRewardAmount().add(total));
            //添加钱包交易流水
            walletLogList.add(new WalletLogs(wallets.getUserId(), null, total, FlowingActionEnum.INCOME.getValue(), FlowingTypeEnum.WEEK.getValue()));
        }
        if (allIdList.size() > 0) {
            //修改未结算的流水记录为已结算
            profitLogsService.updateUseByIdList(allIdList);
        }
        //提交钱包静态收益金额变更记录
        walletService.updateBatchById(walletsList);
        //提交钱包流水日志
        walletLogsService.saveBatch(walletLogList);
        log.info("-------------发放周收益给用户任务结束--------------");
    }

    public static void main(String[] args) {

            //小数加减乘除用BigDecimal
            BigDecimal b1 = new BigDecimal("1.8");
            BigDecimal b2 = new BigDecimal("0.1");
            BigDecimal b3 = b1.add(b2);//小数相加
            BigDecimal b4 = b1.subtract(b2);//小数相减
            BigDecimal b5 = b1.multiply(b2);//小数相乘
            System.out.println("1.8 + 0.1 = " + b3);
            System.out.println("1.8 - 0.1 = " + b4);
            System.out.println("1.8 * 0.1 = " + b5);
    }

}
