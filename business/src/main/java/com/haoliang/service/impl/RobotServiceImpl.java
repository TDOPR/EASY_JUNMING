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
                    //???????????????????????????????????????????????????,??????????????????????????????
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

        //??????????????????????????????????????????
        if (wallets.getWalletAmount().compareTo(robotEnum.getPrice())<0) {
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //????????????????????????????????????
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getRobotAmount, robotEnum.getPrice())
                .set(Wallets::getRobotLevel, robotEnum.getLevel())
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(robotEnum.getPrice()))
                .eq(Wallets::getId, wallets.getId());
        walletsService.update(wrapper);

        //??????????????????????????????
        Integer inviteId = appUserMapper.findInviteIdByUserId(userId);
        if (inviteId > 0) {
            Integer level = appUserRebotRefMapper.countByUserId(inviteId);
            level++;
            //0????????????,????????????id??????0?????????????????????
            AppUserRebotRef appUserRebotRef = AppUserRebotRef.builder()
                    .userId(inviteId)
                    .inviteUserId(userId)
                    .level(level)
                    .build();
            //????????????????????????
            appUserRebotRefMapper.insert(appUserRebotRef);
            //???????????????????????????????????????????????????
            BigDecimal rewardRate = ProxyRebotEnum.getRewardProportionByLevel(level);
            //???????????????
            BigDecimal rewardAmount = robotEnum.getPrice().multiply(rewardRate);
            sendPromotionAward(rewardAmount, inviteId);
            updateUserLevelTaskService.insertListByUserId(userId);
        }

        //????????????????????????
        walletLogsService.insertWalletLogs(userId,robotEnum.getPrice(), FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.BUY_ROBOT);
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult upgradeRebot(RobotDTO robotDTO) {
        //??????????????????????????????
        RobotEnum robotEnum = RobotEnum.levelOf(robotDTO.getLevel());
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getRobotAmount, Wallets::getId, Wallets::getWalletAmount);

        //??????????????????????????????????????????????????????
        if (robotEnum.getPrice().compareTo(wallets.getRobotAmount()) <= 0) {
            return JsonResult.failureResult(ReturnMessageEnum.REBOT_LEVEL_ERROR);
        }

        //??????????????????????????????
        BigDecimal differencePrice = robotEnum.getPrice().subtract(wallets.getRobotAmount());
        //?????????????????????????????????????????????
        if (wallets.getWalletAmount().compareTo(differencePrice)<0) {
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //????????????????????????????????????
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getRobotAmount, robotEnum.getPrice())
                .set(Wallets::getRobotLevel, robotEnum.getLevel())
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(differencePrice))
                .eq(Wallets::getId, wallets.getId());
        walletsService.update(wrapper);

        //??????????????????????????????
        AppUserRebotRef appUserRebotRef = appUserRebotRefMapper.findByInviteUserId(userId);
        if (appUserRebotRef != null) {
            //???????????????????????????????????????????????????
            BigDecimal rewardRate = ProxyRebotEnum.getRewardProportionByLevel(appUserRebotRef.getLevel());
            //????????????????????????
            BigDecimal rewardAmount = differencePrice.multiply(rewardRate);
            sendPromotionAward(rewardAmount, appUserRebotRef.getUserId());
            updateUserLevelTaskService.insertListByUserId(userId);
        }

        //????????????????????????
        walletLogsService.insertWalletLogs(userId,differencePrice, FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.UPGRADE_ROBOT);
        return JsonResult.successResult();
    }

    /**
     * ???????????????
     *
     * @param rewardAmount ????????????
     * @param userId       ???????????????Id
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendPromotionAward(BigDecimal rewardAmount, Integer userId) {
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getWalletAmount);
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().add(rewardAmount))
                .eq(Wallets::getUserId, userId);
        walletsService.update(wrapper);

        //????????????????????????
        walletLogsService.insertWalletLogs(userId,rewardAmount, FlowingActionEnum.INCOME, FlowingTypeEnum.ROBOT);
    }
}
