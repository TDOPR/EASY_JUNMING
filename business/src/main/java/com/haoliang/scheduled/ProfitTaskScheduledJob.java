package com.haoliang.scheduled;

import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.common.enums.BooleanEnum;
import com.haoliang.common.util.DateUtil;
import com.haoliang.manager.TradeManager;
import com.haoliang.mapper.BusinessJobMapper;
import com.haoliang.model.BusinessJob;
import com.haoliang.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * @author Dominick Li
 * @Description 量化收益结算定时任务类
 * @CreateTime 2022/11/1 17:13
 **/
@Component
@Slf4j
public class ProfitTaskScheduledJob {

    @Autowired
    private DayRateService dayRateService;

    @Autowired
    private StrategyService strategyService;

    @Resource
    private BusinessJobMapper businessJobMapper;

    @Autowired
    private TradeManager tradeManager;

    /**
     * 每天凌晨0点5分更新收益率和机器人做单策略
     */
    @Scheduled(cron = "0 5 0 * * ?")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    public void randowDayRate() {
        LocalDate localDate = LocalDate.now();
        dayRateService.insertDayRate(localDate);
        strategyService.insertStrategy(localDate);
    }

    /**
     * 每天凌晨0.10 结算昨天的静态收益
     */
    @Scheduled(cron = "0 10 0 * * ?")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void calculationDayProfit() {
        LocalDate ayerDate = DateUtil.getAverLocalDate();
        BusinessJob businessJob = businessJobMapper.selectOne(new LambdaQueryWrapper<BusinessJob>().eq(BusinessJob::getCreateDate, ayerDate));
        if (businessJob == null) {
            tradeManager.sendStaticTask(ayerDate);
        }
    }

    /**
     * 每天凌晨 1.30 发放代数奖
     */
    @Scheduled(cron = "* 30 1 * * ?")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void sendAlgebraTask() {
        //查询昨天的静态收益是否发放完成
        LocalDate ayerDate = DateUtil.getAverLocalDate();
        Integer reAttempt = 3;
        BusinessJob businessJob;
        do {
            businessJob = businessJobMapper.selectOne(new LambdaQueryWrapper<BusinessJob>().eq(BusinessJob::getCreateDate, ayerDate).eq(BusinessJob::getAlgebraTask, BooleanEnum.FALSE.getIntValue()));
            if (businessJob != null) {
                //发放今天的代数收益
                tradeManager.sendAlgebraTask(businessJob);
            } else {
                reAttempt--;
                //休眠等待30分钟
                ThreadUtil.sleep(30, TimeUnit.MINUTES);
            }
            //自旋3次,避免静态收益还在结算中到账代数奖任务就触发了
        } while (businessJob == null && reAttempt > 0);
    }

    /**
     * 每天凌晨2点发放团队奖
     */
    @Scheduled(cron = "* 0 2 * * ?")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void sendTeamTask() {
        LocalDate ayerDate = DateUtil.getAverLocalDate();
        Integer reAttempt = 3;
        BusinessJob businessJob;
        do {
            businessJob = businessJobMapper.selectOne(new LambdaQueryWrapper<BusinessJob>().eq(BusinessJob::getCreateDate, ayerDate).eq(BusinessJob::getTeamTask, BooleanEnum.FALSE.getIntValue()));
            if (businessJob != null) {
                //发放今天的团队收益
                tradeManager.sendTeamTask(businessJob);
            } else {
                reAttempt--;
                //休眠等待30分钟
                ThreadUtil.sleep(30, TimeUnit.MINUTES);
            }
            //自旋3次,避免静态收益还在结算中到账代数奖任务就触发了
        } while (businessJob == null && reAttempt > 0);
    }

    /**
     * 每天凌晨2.30点发放分红奖
     */
    @Scheduled(cron = "* 30 2 * * ?")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void sendSpecialTask() {
        LocalDate ayerDate = DateUtil.getAverLocalDate();
        Integer reAttempt = 3;
        BusinessJob businessJob;
        do {
            businessJob = businessJobMapper.selectOne(new LambdaQueryWrapper<BusinessJob>().eq(BusinessJob::getCreateDate, ayerDate).eq(BusinessJob::getSpecialTask, BooleanEnum.FALSE.getIntValue()));
            if (businessJob != null) {
                //发放今天的分红收益
                tradeManager.sendSpecialTask(businessJob);
            }//自旋3次,避免静态收益还在结算中到账代数奖任务就触发了
        } while (businessJob == null && reAttempt > 0);
    }

    /**
     * 每周一凌晨2点发放周收益给用户
     */
    @Scheduled(cron = "0 0 2 ? * 1")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void grantToUser() {
        tradeManager.grantToUser();
    }


}
