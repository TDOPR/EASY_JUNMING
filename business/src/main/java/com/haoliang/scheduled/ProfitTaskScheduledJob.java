package com.haoliang.scheduled;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.common.enums.BooleanEnum;
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

/**
 * @author Dominick Li
 * @Description 量化收益结算定时任务类
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
     * 每天晚上21点计算托管静态收益
     */
    @Scheduled(cron = "0 0 21 * * ?")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void calculationDayProfit() {
        BusinessJob businessJob = businessJobMapper.selectOne(new LambdaQueryWrapper<BusinessJob>().eq(BusinessJob::getCreateDate, LocalDate.now()));
        if (businessJob == null) {
            tradeManager.sendStaticTask(LocalDate.now());
        }
    }

    /**
     * 每天晚上21点30发放代数奖
     */
    @Scheduled(cron = "* 30 21 * * ?")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void sendAlgebraTask() {
        //查询今天的静态收益是否发放完成
        BusinessJob businessJob = businessJobMapper.selectOne(new LambdaQueryWrapper<BusinessJob>().eq(BusinessJob::getCreateDate, LocalDate.now()).eq(BusinessJob::getAlgebraTask, BooleanEnum.FALSE.getIntValue()));
        if (businessJob != null) {
            //发放今天的代数收益
            tradeManager.sendAlgebraTask(businessJob);
        }
    }

    /**
     * 每天晚上22点发放团队奖
     */
    //@Scheduled(cron = "* 0 22 * * ?")
    @Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void sendTeamTask() {
        //查询今天的静态收益是否发放完成
        BusinessJob businessJob = businessJobMapper.selectOne(new LambdaQueryWrapper<BusinessJob>().eq(BusinessJob::getCreateDate, LocalDate.now()).eq(BusinessJob::getTeamTask, BooleanEnum.FALSE.getIntValue()));
        if (businessJob != null) {
            //发放今天的团队收益
            tradeManager.sendTeamTask(businessJob);
        }
    }

    /**
     * 每天晚上22.30点发放分红奖
     */
    @Scheduled(cron = "* 30 22 * * ?")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void sendSpecialTask() {
        //查询今天的静态收益是否发放完成
        BusinessJob businessJob = businessJobMapper.selectOne(new LambdaQueryWrapper<BusinessJob>().eq(BusinessJob::getCreateDate, LocalDate.now()).eq(BusinessJob::getSpecialTask, BooleanEnum.FALSE.getIntValue()));
        if (businessJob != null) {
            //发放今天的分红收益
            tradeManager.sendSpecialTask(businessJob);
        }
    }

    /**
     * 每周日晚上10点发放周收益给用户
     */
    @Scheduled(cron = "0 0 22 ? * 7")
    //@Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void grantToUser() {
        tradeManager.grantToUser();
    }


}
