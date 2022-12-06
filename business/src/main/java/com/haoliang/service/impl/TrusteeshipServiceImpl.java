package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.common.util.NumberUtil;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.RobotEnum;
import com.haoliang.mapper.ProfitLogsMapper;
import com.haoliang.model.Strategy;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.AmountDTO;
import com.haoliang.model.vo.TrusteeshipAmountVO;
import com.haoliang.service.*;
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
 * @CreateTime 2022/11/17 11:10
 **/
@Service
public class TrusteeshipServiceImpl implements TrusteeshipService {

    @Autowired
    private WalletsService walletsService;

    @Autowired
    private WalletLogsService walletLogsService;

    @Autowired
    private StrategyService strategyService;

    @Autowired
    private UpdateUserLevelTaskService updateUserLevelTaskService;

    @Resource
    private ProfitLogsMapper profitLogsMapper;

    @Override
    @Transactional
    public JsonResult recharge(AmountDTO amountDTO, String token) {
        //充值到托管金额不能低于10$
        if (amountDTO.getAmount().intValue() < EasyTradeConfig.MIN_AMOUNT) {
            return JsonResult.failureResult(ReturnMessageEnum.MIN_AMOUNT);
        }

        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount, Wallets::getRobotLevel, Wallets::getPrincipalAmount);
        if (amountDTO.getAmount().subtract(wallets.getWalletAmount()).doubleValue() > 0) {
            //如果提现的金额超过托管金额,则返回错误码
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //根据机器人等级获取机器人枚举对象
        RobotEnum robotEnum = RobotEnum.levelOf(wallets.getRobotLevel());
        if (robotEnum != RobotEnum.FIVE && wallets.getPrincipalAmount().add(amountDTO.getAmount()).compareTo(robotEnum.getRechargeMax()) > 0) {
            //如果机器人等级不是4级并且托管的总金额超过机器人支持的上限 需要托管的总金额超出托管金额上限
            return JsonResult.failureResult(ReturnMessageEnum.BEYOND_ROBOT_UPPER_LIMIT);
        }

        //重钱包中扣掉余额添加到托管金额中
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getPrincipalAmount, wallets.getPrincipalAmount().add(amountDTO.getAmount()))
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(amountDTO.getAmount()))
                .eq(Wallets::getId, wallets.getId());
        walletsService.update(wrapper);

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(amountDTO.getAmount(), userId, FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.ENTRUSTMENT);

        updateUserLevelTaskService.insertListByUserId(userId);
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult withdrawal(AmountDTO amountDTO, String token) {
        //提现到钱包
        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount, Wallets::getPrincipalAmount);
        if (amountDTO.getAmount().subtract(wallets.getPrincipalAmount()).doubleValue() > 0) {
            //如果提现的金额超过托管金额,则返回错误码
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //重托管本金中扣除金额添加到钱包余额中
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getPrincipalAmount, wallets.getPrincipalAmount().subtract(amountDTO.getAmount()))
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().add(amountDTO.getAmount()))
                .eq(Wallets::getId, wallets.getId());
        walletsService.update(wrapper);

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(amountDTO.getAmount(), userId, FlowingActionEnum.INCOME, FlowingTypeEnum.WITHDRAWL_WALLET);
        updateUserLevelTaskService.insertListByUserId(userId);
        return JsonResult.successResult();
    }

    @Override
    public JsonResult<TrusteeshipAmountVO> getTrusteeshipAmount(String token) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getPrincipalAmount, Wallets::getRobotLevel);

        String robotName = "";
        List<Strategy> strategyList;
        if (wallets.getRobotLevel() > 0) {
            RobotEnum robotEnum = RobotEnum.levelOf(wallets.getRobotLevel());
            robotName = robotEnum.getName();
            strategyList=strategyService.getStrategyListByRobotLevel(wallets.getRobotLevel());
        }else{
            strategyList=new ArrayList<>();
        }

        //获取量化总收益
        BigDecimal sum = profitLogsMapper.getTotal(userId);
        String profitRate="0.0%";
        if (sum.compareTo(BigDecimal.ZERO)>0) {
            profitRate=sum.divide(wallets.getPrincipalAmount(), 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(1, BigDecimal.ROUND_FLOOR) + "%";
        }

        return JsonResult.successResult(TrusteeshipAmountVO.builder()
                .amount(NumberUtil.toTwoDecimal(wallets.getPrincipalAmount()))
                .profit(NumberUtil.toTwoDecimal(sum))
                .profitRate(profitRate)
                .robotName(robotName)
                .strategyList(strategyList)
                .build());
    }

}
