package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.AlgebraEnum;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.ProxyLevelEnum;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.mapper.UpdateUserLevelTaskMapper;
import com.haoliang.mapper.WalletsMapper;
import com.haoliang.model.AppUsers;
import com.haoliang.model.TreePath;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.AppUsersAmountDTO;
import com.haoliang.model.dto.MyTeamAmountDTO;
import com.haoliang.model.dto.TeamAmountDTO;
import com.haoliang.service.AsyncService;
import com.haoliang.service.TreePathService;
import com.haoliang.service.WalletLogsService;
import com.haoliang.service.WalletsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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

    @Resource
    private UpdateUserLevelTaskMapper updateUserLevelTaskMapper;

    @Resource
    private AppUserMapper appUserMapper;

    @Resource
    private WalletsMapper walletsMapper;


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
                    walletLogsService.insertWalletLogs(wallets.getUserId(), totalIncome, FlowingActionEnum.INCOME, FlowingTypeEnum.ALGEBRA);
                    log.info("发放代数奖: userId={} ,amount={} ", wallets.getUserId(), totalIncome);
                }
            }

            //*****************************发放团队奖和领导奖*************************************
            {
                List<TreePath> firstTreePath = treePathList.stream().filter(treePath -> treePath.getLevel() == 1).collect(Collectors.toList());
                //需要团队数量大于1的才有资格发放团队奖和特别奖
                if (firstTreePath.size() > 1) {
                    //获取我的团队收益情况 TODO 根据历史数据进行计算
                    MyTeamAmountDTO myTeamAmountDTO = walletsService.getMyItemAmount(firstTreePath.stream().map(TreePath::getDescendant).collect(Collectors.toList()));
                    AppUsers appUsers = appUserMapper.selectOne(new LambdaQueryWrapper<AppUsers>().select(AppUsers::getLevel, AppUsers::getMinTeamAmount, AppUsers::getTeamTotalAmount));
                    if (appUsers.getLevel() < ProxyLevelEnum.ONE.getLevel()) {
                        //业绩未达到最低代理商等级
                        log.info("团队业绩未达标: userId={},amount={}", wallets.getUserId(), appUsers.getMinTeamAmount());
                    } else {
                        flag = true;
                        //计算小团队的量化收益总量
                        BigDecimal minProfit = BigDecimal.ZERO, amount;
                        for (Integer userId : myTeamAmountDTO.getMinItemUserIdList()) {
                            amount = userAmount.get(userId);
                            if (amount != null) {
                                minProfit = minProfit.add(amount);
                            }
                        }

                        //根据代理商等级对应的收益比计算发放给客户的团队奖  =小团队的量化收益 * 收益率
                        ProxyLevelEnum proxyLevelEnum = ProxyLevelEnum.getByLevel(appUsers.getLevel());
                        BigDecimal sendAmount = minProfit.multiply(proxyLevelEnum.getIncomeRatio());
                        wallets.setWalletAmount(wallets.getWalletAmount().add(sendAmount));
                        walletLogsService.insertWalletLogs(wallets.getUserId(), sendAmount, FlowingActionEnum.INCOME, FlowingTypeEnum.TEAM);
                        log.info("发放团队奖: userId={} ,amount={} ", wallets.getUserId(), sendAmount);

                        //判断代理商等级是否有资格发放特别奖  需要级别在4以上才能分红
                        if (proxyLevelEnum.getLevel() >= ProxyLevelEnum.FOUR.getLevel()) {
                            BigDecimal allProfit = minProfit;
                            //统计大团队的收益
                            for (Integer userId : myTeamAmountDTO.getMaxItemUserIdList()) {
                                amount = userAmount.get(userId);
                                if (amount != null) {
                                    allProfit = allProfit.add(amount);
                                }
                            }
                            sendAmount = allProfit.multiply(EasyTradeConfig.SPECIAL_AWARD_RATE);
                            wallets.setWalletAmount(wallets.getWalletAmount().add(sendAmount));
                            walletLogsService.insertWalletLogs(wallets.getUserId(), sendAmount, FlowingActionEnum.INCOME, FlowingTypeEnum.SPECIAL);
                            log.info("发放领导奖: userId={} ,amount={} ", wallets.getUserId(), sendAmount);
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


    @Override
    @Async("asyncExecutor")
    public void updateUserLevelTask(Integer userId, CountDownLatch countDownLatch) {
        try {
            Long time = System.currentTimeMillis();
            log.info("start updateUserParentLevel userId={} ", userId);
            //查询用户直推用户的信息
            List<AppUsersAmountDTO> appUsersList = walletsMapper.findAchievementByInviteId(userId);
            if (appUsersList.size() < 2) {
                //最少需要两个直推用户才能有代理商等级分红资格
                return;
            }

            //所有团队的收益
            BigDecimal totalAmount, childSumAmount;
            List<TeamAmountDTO> teamAmountDTOS = new ArrayList<>();
            Integer index = 0;
            for (AppUsersAmountDTO appUsersAmountDTO : appUsersList) {
                //获取用户业绩
                totalAmount = appUsersAmountDTO.getTotalAmount();
                //获取用户所有下级业绩
                childSumAmount = walletsMapper.getMyItemAmountByUserId(appUsersAmountDTO.getUserId());
                if (childSumAmount != null) {
                    totalAmount = totalAmount.add(childSumAmount);
                }
                teamAmountDTOS.add(new TeamAmountDTO(totalAmount, index));
                index++;
            }

            //找到大团队的金额
            TeamAmountDTO max = teamAmountDTOS.stream().max(Comparator.comparing(x -> x.getItemIncome())).get();
            //移除大团队的金额
            teamAmountDTOS.remove(max);

            //获取小团队的所有金额
            BigDecimal minTeamAmount = teamAmountDTOS.stream().map(TeamAmountDTO::getItemIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
            ProxyLevelEnum proxyLevelEnum = ProxyLevelEnum.valueOfByAmount(minTeamAmount);
            UpdateWrapper<AppUsers> updateWrapper = Wrappers.update();
            updateWrapper.lambda()
                    .set(AppUsers::getMinTeamAmount, minTeamAmount)
                    .set(AppUsers::getTeamTotalAmount, minTeamAmount.add(max.getItemIncome()))
                    .eq(AppUsers::getId, userId);

            //需要修改Tree中直推一代中的大小团队标识
            AppUsersAmountDTO maxUserAmount = appUsersList.get(max.getIndex());
            appUsersList.remove(maxUserAmount);
            List<Integer> minUserIds = appUsersList.stream().map(AppUsersAmountDTO::getUserId).collect(Collectors.toList());

            //修改为大团队
            UpdateWrapper<TreePath> treePathUpdateWrapper = Wrappers.update();
            treePathUpdateWrapper.lambda()
                    .set(TreePath::getLargeTeam, 1)
                    .eq(TreePath::getAncestor, userId)
                    .eq(TreePath::getDescendant, maxUserAmount.getUserId());
            treePathService.update(treePathUpdateWrapper);

            //修改为小团队
            treePathUpdateWrapper = Wrappers.update();
            treePathUpdateWrapper.lambda()
                    .set(TreePath::getLargeTeam, 0)
                    .eq(TreePath::getAncestor, userId)
                    .in(TreePath::getDescendant, minUserIds);
            treePathService.update(treePathUpdateWrapper);

            if (proxyLevelEnum == null) {
                //业绩未达标不发放金额
                log.info("团队业绩未达成代理商最低等级限制: userId={},useramount:{} < {}", userId, minTeamAmount, ProxyLevelEnum.ONE.getMoney());
            } else {
                log.info("团队业绩达标，代理商等级升级为:{}: userId={},amount={}", proxyLevelEnum.getLevel(), userId, minTeamAmount);
                updateWrapper.lambda().set(AppUsers::getLevel, proxyLevelEnum.getLevel());
            }
            appUserMapper.update(null, updateWrapper);
            //更新完后删除当前任务
            updateUserLevelTaskMapper.deleteById(userId);
            log.info("start updateUserParentLevel userId={} time={} ", userId, System.currentTimeMillis() - time);
        } finally {
            countDownLatch.countDown();
        }
    }


}
