package com.haoliang.manager;

import cn.hutool.core.date.TimeInterval;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haoliang.common.enums.BooleanEnum;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.AlgebraEnum;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.ProxyLevelEnum;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.mapper.BusinessJobMapper;
import com.haoliang.model.*;
import com.haoliang.model.dto.TreePathAmountDTO;
import com.haoliang.model.dto.UserWalletsDTO;
import com.haoliang.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dominick Li
 * @Description 核心业务类
 * @CreateTime 2022/12/5 16:18
 **/
@Slf4j
@Component
public class TradeManager {

    @Autowired
    private WalletsService walletsService;

    @Autowired
    private TreePathService treePathService;

    @Autowired
    private WalletLogsService walletLogsService;

    @Autowired
    private DayRateService dayRateService;

    @Resource
    private ProfitLogsService profitLogsService;

    @Resource
    private BusinessJobMapper businessJobMapper;

    @Resource
    private AppUserMapper appUserMapper;

    /**
     * 发放当天用户的托管静态收益
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendStaticTask(LocalDate localDate) {
        log.info("-------------计算日收益任务开始--------------");
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start();

        //查询托管金额大于0的用户钱包信息
        List<Wallets> walletsList = walletsService.list(new LambdaQueryWrapper<Wallets>()
                .select(
                        Wallets::getPrincipalAmount,
                        Wallets::getUserId,
                        Wallets::getRobotLevel
                )
                .gt(Wallets::getPrincipalAmount, 0));
        long selectTime = timeInterval.intervalRestart();

        List<ProfitLogs> profitLogsList = new ArrayList<>(walletsList.size());

        //当天产生的静态收益
        BigDecimal amount;
        DayRate dayRate = dayRateService.selectNewDayRate();
        for (Wallets wallets : walletsList) {
            //根据托管金额和机器人对应的利率计算收益金额
            amount = wallets.getPrincipalAmount().multiply(dayRateService.getDayRateByLevel(dayRate, wallets.getRobotLevel()));
            profitLogsList.add(
                    ProfitLogs.builder()
                    .userId(wallets.getUserId())
                    .principal(wallets.getPrincipalAmount())
                    .generatedAmount(amount)
                    .createDate(localDate)
                    .build()
            );
        }
        long computeTime = timeInterval.intervalRestart();

        //批量插入日收益信息
        profitLogsService.saveBatch(profitLogsList);
        //添加当天任务处理成功标识
        BusinessJob businessJob = new BusinessJob();
        businessJob.setCreateDate(LocalDate.now());
        businessJobMapper.insert(businessJob);
        long saveTime = timeInterval.intervalRestart();
        log.info("-------------计算日收益任务结束 select times:{} ms ,compute times:{} ms,save times:{} ms,total:{} ms--------------", selectTime, computeTime, saveTime, (selectTime + computeTime + saveTime));
    }

    /**
     * 发放当天的代数奖
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendAlgebraTask(BusinessJob businessJob) {
        log.info("-------------开始发放代数奖--------------");
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start();

        //获取有托管金额大于300的用户
        List<Wallets> walletsList = walletsService.list(new LambdaQueryWrapper<Wallets>()
                .select(
                        Wallets::getUserId,
                        Wallets::getWalletAmount
                )
                .gt(Wallets::getPrincipalAmount, EasyTradeConfig.PROXY_MIN_MONEY)
        );
        long selectTime = timeInterval.intervalRestart();

        List<WalletLogs> walletLogsList = new ArrayList<>();
        List<TreePathAmountDTO> treePathList;
        BigDecimal totalIncome;

        for (Wallets wallets : walletsList) {
            //获取团队指定代数静态收益
            treePathList = treePathService.getProfitAmountByUserIdAndLevelList(wallets.getUserId(), businessJob.getCreateDate(), EasyTradeConfig.ALGEBRA_LEVEL);
            totalIncome = new BigDecimal("0");
            for (TreePathAmountDTO treePathAmountDTO : treePathList) {
                totalIncome = totalIncome.add(treePathAmountDTO.getTotalAmount().multiply(AlgebraEnum.getRechargeMaxByLevel(treePathAmountDTO.getLevel())));
            }

            //金额大于零则发放给用户
            if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
                //更新钱包余额
                walletsService.lookUpdateWallets(wallets.getUserId(), totalIncome, FlowingActionEnum.INCOME);

                //插入流水变更记录
                walletLogsList.add(
                        WalletLogs.builder()
                                .userId(wallets.getUserId())
                                .amount(totalIncome)
                                .action(FlowingActionEnum.INCOME.getValue())
                                .type(FlowingTypeEnum.ALGEBRA.getValue())
                                .build()
                );
                log.info("发放代数奖: userId={} ,amount={} ", wallets.getUserId(), totalIncome);
            }
        }
        long computeTime = timeInterval.intervalRestart();

        //插入钱包流水变更记录
        if (walletLogsList.size() > 0) {
            walletLogsService.saveBatch(walletLogsList);
        }

        //更新发放代数奖任务完成
        UpdateWrapper<BusinessJob> jobUpdateWrapper = Wrappers.update();
        jobUpdateWrapper.lambda()
                .set(BusinessJob::getAlgebraTask, BooleanEnum.TRUE.getIntValue())
                .eq(BusinessJob::getCreateDate, businessJob.getCreateDate());
        businessJobMapper.update(null, jobUpdateWrapper);
        long saveTime = timeInterval.intervalRestart();
        log.info("-------------结束发放代数奖 select times:{} ms ,compute times:{} ms,save times:{} ms,total:{} ms--------------", selectTime, computeTime, saveTime, (selectTime + computeTime + saveTime));
    }

    /**
     * 发放当天的团队奖
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendTeamTask(BusinessJob businessJob) {
        log.info("-------------开始发放团队奖--------------");
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start();

        //代理商等级大于0的用户才有资格发放团队奖
        List<UserWalletsDTO> walletsList = walletsService.selectUserWalletsDTOListByUserLevelGtAndPrincipalAmountGe(0, EasyTradeConfig.PROXY_MIN_MONEY);
        long selectTime = timeInterval.intervalRestart();

        //小团队量化收益,要发放给供应商的量化收益
        BigDecimal minTeamTotalProfitAmount, sendAmount;
        List<WalletLogs> walletLogsList = new ArrayList<>();

        for (UserWalletsDTO userWalletsDTO : walletsList) {
            minTeamTotalProfitAmount = treePathService.getMinTeamTotalProfitAmount(userWalletsDTO.getUserId(), businessJob.getCreateDate());
            //收益大于零的时候发放给用户
            if (minTeamTotalProfitAmount.compareTo(BigDecimal.ZERO) > 0) {
                sendAmount = minTeamTotalProfitAmount.multiply(ProxyLevelEnum.getByLevel(userWalletsDTO.getLevel()).getIncomeRatio());
                //更新钱包余额
                walletsService.lookUpdateWallets(userWalletsDTO.getUserId(), sendAmount, FlowingActionEnum.INCOME);

                walletLogsList.add(
                        WalletLogs.builder()
                                .userId(userWalletsDTO.getUserId())
                                .amount(sendAmount)
                                .action(FlowingActionEnum.INCOME.getValue())
                                .type(FlowingTypeEnum.TEAM.getValue())
                                .build()
                );
                log.info("发放团队奖: userId={} ,amount={} ", userWalletsDTO.getUserId(), sendAmount);
            }
        }
        long computeTime = timeInterval.intervalRestart();

        //插入钱包流水变更记录
        if (walletLogsList.size() > 0) {
            walletLogsService.saveBatch(walletLogsList);
        }

        //更新发放团队奖任务完成
        UpdateWrapper<BusinessJob> jobUpdateWrapper = Wrappers.update();
        jobUpdateWrapper.lambda()
                .set(BusinessJob::getTeamTask, BooleanEnum.TRUE.getIntValue())
                .eq(BusinessJob::getCreateDate, businessJob.getCreateDate());
        businessJobMapper.update(null, jobUpdateWrapper);
        long saveTime = timeInterval.intervalRestart();
        log.info("-------------结束发放团队奖 select times:{} ms ,compute times:{} ms,save times:{} ms,total:{} ms--------------", selectTime, computeTime, saveTime, (selectTime + computeTime + saveTime));
    }

    /**
     * 发放今天的分红奖励
     */
    public void sendSpecialTask(BusinessJob businessJob) {
        log.info("-------------开始发放分红奖--------------");
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start();

        //供应商等级4和5级才有资格发放分红奖
        List<UserWalletsDTO> walletsList = walletsService.selectUserWalletsDTOListByUserLevelGtAndPrincipalAmountGe(ProxyLevelEnum.THREE.getLevel(), EasyTradeConfig.PROXY_MIN_MONEY);
        long selectTime = timeInterval.intervalRestart();

        //团队总量化收益,该发放的分红奖励
        BigDecimal teamTotalProfitAmount, sendAmount;
        List<WalletLogs> walletLogsList = new ArrayList<>();

        for (UserWalletsDTO userWalletsDTO : walletsList) {
            teamTotalProfitAmount = treePathService.getTeamTotalProfitAmount(userWalletsDTO.getUserId(), businessJob.getCreateDate());
            if (teamTotalProfitAmount.compareTo(BigDecimal.ZERO) > 0) {
                sendAmount = teamTotalProfitAmount.multiply(EasyTradeConfig.SPECIAL_AWARD_RATE);
                //更新钱包余额
                walletsService.lookUpdateWallets(userWalletsDTO.getUserId(), sendAmount, FlowingActionEnum.INCOME);

                walletLogsList.add(
                        WalletLogs.builder()
                                .userId(userWalletsDTO.getUserId())
                                .amount(sendAmount)
                                .action(FlowingActionEnum.INCOME.getValue())
                                .type(FlowingTypeEnum.SPECIAL.getValue())
                                .build()
                );
                log.info("发放分红奖: userId={} ,amount={} ", userWalletsDTO.getUserId(), sendAmount);
            }
        }
        long computeTime = timeInterval.intervalRestart();

        //插入钱包流水变更记录
        if (walletLogsList.size() > 0) {
            walletLogsService.saveBatch(walletLogsList);
        }

        //更新发放分红奖任务完成
        UpdateWrapper<BusinessJob> jobUpdateWrapper = Wrappers.update();
        jobUpdateWrapper.lambda()
                .set(BusinessJob::getSpecialTask, BooleanEnum.TRUE.getIntValue())
                .eq(BusinessJob::getCreateDate, businessJob.getCreateDate());
        businessJobMapper.update(null, jobUpdateWrapper);
        long saveTime = timeInterval.intervalRestart();
        log.info("-------------结束发放分红奖 select times:{} ms ,compute times:{} ms,save times:{} ms,total:{} ms--------------", selectTime, computeTime, saveTime, (selectTime + computeTime + saveTime));
    }

    /**
     * 结算量化收益给客户
     */
    public void grantToUser() {
        log.info("-------------发放周收益给用户任务开始--------------");
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start();

        List<AppUsers> appUsersList = appUserMapper.selectList(new LambdaQueryWrapper<AppUsers>().select(AppUsers::getId).eq(AppUsers::getEnabled, BooleanEnum.TRUE.getIntValue()));
        long selectTime = timeInterval.intervalRestart();

        List<ProfitLogs> profitLogsList;
        List<Long> allIdList = new ArrayList<>();
        BigDecimal total;
        List<WalletLogs> walletLogList = new ArrayList<>();
        WalletLogs walletLogs;
        for (AppUsers appUsers : appUsersList) {
            //获取用户未结算的收益信息
            profitLogsList = profitLogsService.list(new LambdaQueryWrapper<ProfitLogs>().eq(ProfitLogs::getStatus, 0).eq(ProfitLogs::getUserId, appUsers.getId()));
            allIdList.addAll(profitLogsList.stream().map(ProfitLogs::getId).collect(Collectors.toList()));
            //获取周收益金额
            total = new BigDecimal("0.0");
            for (ProfitLogs profitLogs : profitLogsList) {
                total = total.add(profitLogs.getGeneratedAmount());
            }
            //更新钱包余额
            walletsService.lookUpdateWallets(appUsers.getId(), total, FlowingActionEnum.INCOME);

            walletLogs = WalletLogs.builder()
                    .userId(appUsers.getId())
                    .amount(total)
                    .action(FlowingActionEnum.INCOME.getValue())
                    .type(FlowingTypeEnum.STATIC.getValue())
                    .build();
            //添加钱包交易流水
            walletLogList.add(walletLogs);
        }
        long computeTime = timeInterval.intervalRestart();

        if (allIdList.size() > 0) {
            //修改未结算的流水记录为已结算
            profitLogsService.updateUseByIdList(allIdList);
        }

        //提交钱包流水日志
        walletLogsService.saveBatch(walletLogList);
        long saveTime = timeInterval.intervalRestart();
        log.info("-------------发放周收益给用户任务结束 select times:{} ms ,compute times:{} ms,save times:{} ms,total:{} ms--------------", selectTime, computeTime, saveTime, (selectTime + computeTime + saveTime));
    }
}
