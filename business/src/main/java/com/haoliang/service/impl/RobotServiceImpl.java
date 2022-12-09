package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.ProxyRebotEnum;
import com.haoliang.enums.RobotEnum;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.mapper.AppUserRebotRefMapper;
import com.haoliang.model.AppUserRebotRef;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.RobotDTO;
import com.haoliang.model.vo.RobotDetailVO;
import com.haoliang.service.RobotService;
import com.haoliang.service.UpdateUserLevelTaskService;
import com.haoliang.service.WalletLogsService;
import com.haoliang.service.WalletsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/17 10:55
 **/
@Service
public class RobotServiceImpl implements RobotService {

    @Autowired
    private WalletsService walletsService;

    @Autowired
    private WalletLogsService walletLogsService;

    @Resource
    private AppUserMapper appUserMapper;

    @Resource
    private AppUserRebotRefMapper appUserRebotRefMapper;

    @Autowired
    private UpdateUserLevelTaskService updateUserLevelTaskService;

    @Override
    public JsonResult getRobotList() {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getRobotLevel);
        List<RobotDetailVO.RobotVO> robotList = new ArrayList<>();
        RobotEnum hasRobotEnum = RobotEnum.levelOf(wallets.getRobotLevel());
        for (RobotEnum robotEnum : RobotEnum.values()) {
            if (robotEnum == RobotEnum.ZERO) {
                continue;
            }
            robotList.add(RobotDetailVO.RobotVO.builder()
                    .level(robotEnum.getLevel())
                    .price(robotEnum.getPrice())
                    .rechargeMax("$"+robotEnum.getRechargeMax())
                    //如果机器人等级小于等于已购买的等级,则设置为不可购买状态
                    .selected(robotEnum.getLevel() <= hasRobotEnum.getLevel())
                    .build());
        }
        RobotDetailVO robotDetailVO = new RobotDetailVO();
        robotDetailVO.setPrice(hasRobotEnum.getPrice());
        robotDetailVO.setRobotList(robotList);
        return JsonResult.successResult(robotDetailVO);
    }

    @Override
    @Transactional
    public JsonResult buyRebot(RobotDTO robotDTO) {
        RobotEnum robotEnum = RobotEnum.levelOf(robotDTO.getLevel());
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount, Wallets::getRobotLevel);
        if (wallets.getRobotLevel() > 0) {
            return JsonResult.failureResult(ReturnMessageEnum.REPEAT_PURCHASE_REBOT);
        }

        //判断钱包余额是否够能买机器人
        if (wallets.getWalletAmount().compareTo(robotEnum.getPrice())<0) {
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //设置机器人信息和扣除差价
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getRobotAmount, robotEnum.getPrice())
                .set(Wallets::getRobotLevel, robotEnum.getLevel())
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(robotEnum.getPrice()))
                .eq(Wallets::getId, wallets.getId());
        walletsService.update(wrapper);

        //判断用户是否有邀请人
        Integer inviteId = appUserMapper.findInviteIdByUserId(userId);
        if (inviteId > 0) {
            Integer level = appUserRebotRefMapper.countByUserId(inviteId);
            level++;
            //0是默认值,当邀请人id大于0则表示有邀请人
            AppUserRebotRef appUserRebotRef = AppUserRebotRef.builder()
                    .userId(inviteId)
                    .inviteUserId(userId)
                    .level(level)
                    .build();
            //添加推广位数关系
            appUserRebotRefMapper.insert(appUserRebotRef);
            //根据推广位数要求发放对应的奖励比例
            BigDecimal rewardRate = ProxyRebotEnum.getRewardProportionByLevel(level);
            //发送推广奖
            BigDecimal rewardAmount = robotEnum.getPrice().multiply(rewardRate);
            sendPromotionAward(rewardAmount, inviteId);
            updateUserLevelTaskService.insertListByUserId(userId);
        }

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(userId,robotEnum.getPrice(), FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.BUY_ROBOT);
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult upgradeRebot(RobotDTO robotDTO) {
        //获取需要升级的机器人
        RobotEnum robotEnum = RobotEnum.levelOf(robotDTO.getLevel());
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getRobotAmount, Wallets::getId, Wallets::getWalletAmount);

        //校验升级机器人等级是否比原有的等级高
        if (robotEnum.getPrice().compareTo(wallets.getRobotAmount()) <= 0) {
            return JsonResult.failureResult(ReturnMessageEnum.REBOT_LEVEL_ERROR);
        }

        //计算升级所需要的差价
        BigDecimal differencePrice = robotEnum.getPrice().subtract(wallets.getRobotAmount());
        //判断钱包余额是否够能升级机器人
        if (wallets.getWalletAmount().compareTo(differencePrice)<0) {
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //更新机器人信息和扣除差价
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getRobotAmount, robotEnum.getPrice())
                .set(Wallets::getRobotLevel, robotEnum.getLevel())
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(differencePrice))
                .eq(Wallets::getId, wallets.getId());
        walletsService.update(wrapper);

        //判断用户是否有邀请人
        AppUserRebotRef appUserRebotRef = appUserRebotRefMapper.findByInviteUserId(userId);
        if (appUserRebotRef != null) {
            //根据推广是第几位发放对应的奖励比例
            BigDecimal rewardRate = ProxyRebotEnum.getRewardProportionByLevel(appUserRebotRef.getLevel());
            //发放差价的推广奖
            BigDecimal rewardAmount = differencePrice.multiply(rewardRate);
            sendPromotionAward(rewardAmount, appUserRebotRef.getUserId());
            updateUserLevelTaskService.insertListByUserId(userId);
        }

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(userId,differencePrice, FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.UPGRADE_ROBOT);
        return JsonResult.successResult();
    }

    /**
     * 发送推广奖
     *
     * @param rewardAmount 奖励金额
     * @param userId       发放的用户Id
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendPromotionAward(BigDecimal rewardAmount, Integer userId) {
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getWalletAmount);
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().add(rewardAmount))
                .eq(Wallets::getUserId, userId);
        walletsService.update(wrapper);

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(userId,rewardAmount, FlowingActionEnum.INCOME, FlowingTypeEnum.ROBOT);
    }
}
