package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.IdUtils;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.*;
import com.haoliang.mapper.*;
import com.haoliang.model.*;
import com.haoliang.model.dto.AmountDTO;
import com.haoliang.model.dto.RebotDTO;
import com.haoliang.model.dto.WalletOrderDTO;
import com.haoliang.service.AppUserWithdrawService;
import com.haoliang.service.WalletLogsService;
import com.haoliang.service.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 12:20
 **/
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallets> implements WalletService {

    @Resource
    private AppUserMapper appUserMapper;

    @Resource
    private WalletMapper walletMapper;

    @Resource
    private WalletLogsService walletLogsService;

    @Resource
    private AppUserRechargeMapper appUserRechargeMapper;

    @Resource
    private AddressPoolMapper addressPoolMapper;

    @Resource
    private AppUserWithdrawService appUserWithdrawService;

    @Resource
    private AppUserRebotRefMapper appUserRebotRefMapper;


    @Override
    public JsonResult getMyWallet(String token) {
        Wallets wallets = this.getById(JwtTokenUtils.getUserIdFromToken(token));
        //查询我的团队

        return JsonResult.successResult(wallets);
    }

    @Override
    @Transactional
    public JsonResult recharge(WalletOrderDTO walletOrderDTO, String token) {
        //充值
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        BigDecimal usdAmount;
        BigDecimal exchangeRate;
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount, Wallets::getBlockAddress);
        if (walletOrderDTO.getCoinUnit().equals(CoinUnitEnum.LEGAL_CURRENCY.getType())) {
            usdAmount = walletOrderDTO.getAmount().divide(EasyTradeConfig.XPF_2_USD, 7, RoundingMode.FLOOR);
            exchangeRate = EasyTradeConfig.XPF_2_USD;
            //TODO 法币充值，走第三方支付接口
        } else {
            //判断是否有可用的区块链地址
            boolean hasAddress = hasBlockAddress(wallets);
            if (!hasAddress) {
                return JsonResult.failureResult(ReturnMessageEnum.BLOCK_ADDRESS_EMPTY);
            }
            String address = wallets.getBlockAddress();
            usdAmount = walletOrderDTO.getAmount().divide(EasyTradeConfig.USDT_2_USD, 7, RoundingMode.FLOOR);
            exchangeRate = EasyTradeConfig.USDT_2_USD;
            //TODO Usdt充值
        }

        //生成充值记录
        AppUserRecharge appUserRecharge = AppUserRecharge.builder()
                .exchangeRate(exchangeRate)
                .userId(userId)
                .txid(IdUtils.simpleUUID())
                .coinUnit(walletOrderDTO.getCoinUnit())
                .amount(walletOrderDTO.getAmount())
                .usdAmount(usdAmount)
                .build();
        appUserRechargeMapper.insert(appUserRecharge);

        //把充值的金额往钱包表里余额里增加 TODO 区块链的需要确定充值后是否能及时到账
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().add(usdAmount))
                .eq(Wallets::getId, wallets.getId());
        update( wrapper);


        //添加钱包流水记录
        walletLogsService.insertWalletLogs(usdAmount,userId,FlowingActionEnum.INCOME,FlowingTypeEnum.RECHARGE);
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult withdrawal(WalletOrderDTO walletOrderDTO, String token) {
        //提现
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);

        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount, Wallets::getBlockAddress);

        //根据提现的货币类型计算手续费
        CoinUnitEnum coinUnitEnum = CoinUnitEnum.valueOfByType(walletOrderDTO.getCoinUnit());

        //如果是提现到区块链，获取区块链地址
        if (coinUnitEnum == CoinUnitEnum.USDT) {
            boolean hasAddress = hasBlockAddress(wallets);
            if (!hasAddress) {
                return JsonResult.failureResult(ReturnMessageEnum.BLOCK_ADDRESS_EMPTY);
            }
        }

        //生成提现记录
        AppUserWithdraw appUserWithdraw = AppUserWithdraw.builder()
                .userId(userId)
                .txid(IdUtils.simpleUUID())
                .address(wallets.getBlockAddress())
                .coinUnit(walletOrderDTO.getCoinUnit())
                .amount(walletOrderDTO.getAmount())
                .build();

        if (walletOrderDTO.getAmount().subtract(EasyTradeConfig.AMOUNT_CHECK).doubleValue() > 0) {
            //需要审核,设置提现任务状态为待审核
            appUserWithdraw.setStatus(WithdrawStateEnum.UNDER_REVIEW.getState());
            appUserWithdraw.setAuditStatus(WithdrawStateEnum.UNDER_REVIEW.getState());
            appUserWithdrawService.save(appUserWithdraw);
        } else {
            //小额不需要审核直接走提现逻辑
            appUserWithdrawService.withdraw(appUserWithdraw, wallets);
        }
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult trusteeshipRecharge(AmountDTO amountDTO, String token) {
        //充值到托管金额不能低于10$
        if (amountDTO.getAmount().intValue() < EasyTradeConfig.MIN_AMOUNT) {
            return JsonResult.failureResult(ReturnMessageEnum.MIN_AMOUNT);
        }

        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount, Wallets::getRobotLevel, Wallets::getPrincipalAmount);
        if (amountDTO.getAmount().subtract(wallets.getWalletAmount()).doubleValue() > 0) {
            //如果提现的金额超过托管金额,则返回错误码
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //根据机器人等级获取机器人枚举对象
        RobotEnum robotEnum = RobotEnum.valueOf(wallets.getRobotLevel());
        if (robotEnum != RobotEnum.ROBOT_FOUR && wallets.getPrincipalAmount().add(amountDTO.getAmount()).subtract(robotEnum.getRechargeMax()).intValue() > 0) {
            //如果机器人等级不是4级并且托管的总金额超过机器人支持的上限 需要托管的总金额超出托管金额上限
            return JsonResult.failureResult(ReturnMessageEnum.BEYOND_ROBOT_UPPER_LIMIT);
        }

        //重钱包中扣掉余额添加到托管金额中
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getPrincipalAmount, wallets.getPrincipalAmount().add(amountDTO.getAmount()))
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(amountDTO.getAmount()))
                .eq(Wallets::getId, wallets.getId());
        update( wrapper);

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(amountDTO.getAmount(),userId,FlowingActionEnum.EXPENDITURE,FlowingTypeEnum.ENTRUSTMENT);
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult trusteeshipWithdrawal(AmountDTO amountDTO, String token) {
        //提现到钱包
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount, Wallets::getPrincipalAmount);
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
        update( wrapper);

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(amountDTO.getAmount(),userId,FlowingActionEnum.INCOME,FlowingTypeEnum.WITHDRAWL_WALLET);
        return JsonResult.successResult();
    }


    @Override
    @Transactional
    public JsonResult buyRebot(RebotDTO rebotDTO, String token) {
        RobotEnum robotEnum = RobotEnum.valueOf(rebotDTO.getLevel());
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount, Wallets::getRobotLevel);
        if (wallets.getRobotLevel() > 0) {
            return JsonResult.failureResult(ReturnMessageEnum.REPEAT_PURCHASE_REBOT);
        }

        //判断钱包余额是否够能买机器人
        if (wallets.getWalletAmount().intValue() < robotEnum.getPrice().intValue()) {
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //设置机器人信息和扣除差价
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getRobotAmount, robotEnum.getPrice())
                .set(Wallets::getRobotLevel, robotEnum.getValue())
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(robotEnum.getPrice()))
                .eq(Wallets::getId, wallets.getId());
        update(null, wrapper);

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
            sendPromotionAward(rewardAmount, inviteId, userId);
        }

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(robotEnum.getPrice(),userId,FlowingActionEnum.EXPENDITURE,FlowingTypeEnum.BUY_ROBOT);
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult upgradeRebot(RebotDTO rebotDTO, String token) {
        //获取需要升级的机器人
        RobotEnum robotEnum = RobotEnum.valueOf(rebotDTO.getLevel());
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getRobotAmount, Wallets::getId, Wallets::getWalletAmount);
        if (robotEnum.getPrice().subtract(wallets.getRobotAmount()).intValue() <= 0) {
            return JsonResult.failureResult(ReturnMessageEnum.REBOT_LEVEL_ERROR);
        }
        //计算升级所需要的差价
        BigDecimal differencePrice = robotEnum.getPrice().subtract(wallets.getRobotAmount());
        //判断钱包余额是否够能升级机器人
        if (wallets.getWalletAmount().intValue() < differencePrice.intValue()) {
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //更新机器人信息和扣除差价
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getRobotAmount, robotEnum.getPrice())
                .set(Wallets::getRobotLevel, robotEnum.getValue())
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(differencePrice))
                .eq(Wallets::getId, wallets.getId());
        update(null, wrapper);

        //判断用户是否有邀请人
        AppUserRebotRef appUserRebotRef = appUserRebotRefMapper.findByInviteUserId(userId);
        if (appUserRebotRef != null) {
            //根据推广位数要求发放对应的奖励比例
            BigDecimal rewardRate = ProxyRebotEnum.getRewardProportionByLevel(appUserRebotRef.getLevel());
            //发放差价的推广奖
            BigDecimal rewardAmount = differencePrice.multiply(rewardRate);
            sendPromotionAward(rewardAmount, appUserRebotRef.getUserId(), userId);
        }

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(differencePrice,userId,FlowingActionEnum.EXPENDITURE,FlowingTypeEnum.UPGRADE_ROBOT);
        return JsonResult.successResult();
    }


    /**
     * 判断是否有区块链地址，如果没有则重地址池中取出然后设置到钱包里
     *
     * @param wallets
     * @return
     */
    private boolean hasBlockAddress(Wallets wallets) {
        String address = wallets.getBlockAddress();
        if (address == null) {
            address = addressPoolMapper.getAddress();
            if (address == null) {
                return false;
            }
            addressPoolMapper.deleteByAddress(address);
            wallets.setBlockAddress(address);
            UpdateWrapper<Wallets> wrapper = Wrappers.update();
            wrapper.lambda()
                    .set(Wallets::getBlockAddress, address)
                    .eq(Wallets::getId, wallets.getId());
            walletMapper.update(null, wrapper);
        }
        return true;
    }

    /**
     * 发送推广奖
     *
     * @param rewardAmount 奖励金额
     * @param userId       发放的用户Id
     * @param targetUserId 购买机器人的用户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void sendPromotionAward(BigDecimal rewardAmount, Integer userId, Integer targetUserId) {
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getWalletAmount);
        UpdateWrapper<Wallets> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(Wallets::getWalletAmount, wallets.getWalletAmount().add(rewardAmount))
                .eq(Wallets::getId, wallets.getId());
        update(null, wrapper);

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(rewardAmount,userId,FlowingActionEnum.INCOME,FlowingTypeEnum.ROBOT);
    }

    /**
     * 根据用户ID查询指定列数据
     *
     * @param userId  用户Id
     * @param columns 需要查询的指定列
     * @return
     */
    @Override
    public Wallets selectColumnsByUserId(Integer userId, SFunction<Wallets, ?>... columns) {
        Wallets wallets = walletMapper.selectOne(new LambdaQueryWrapper<Wallets>().select(columns).eq(Wallets::getUserId, userId));
        return wallets;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWallet(BigDecimal amount, Integer userId,FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum) {
        Wallets wallets=this.getOne(new LambdaQueryWrapper<Wallets>().select(Wallets::getId,Wallets::getWalletAmount).eq(Wallets::getUserId,userId));
        UpdateWrapper<Wallets> walletsUpdateWrapper=Wrappers.update();
        BigDecimal result;
        if(flowingActionEnum==FlowingActionEnum.INCOME){
            result=wallets.getWalletAmount().add(amount);
        }else{
            result=wallets.getWalletAmount().subtract(amount);
        }
        walletsUpdateWrapper.lambda()
                .set(Wallets::getWalletAmount,result)
                .eq(Wallets::getId,wallets.getId());
        this.update(walletsUpdateWrapper);
        //插入流水记录
        walletLogsService.insertWalletLogs(amount,userId,flowingActionEnum,flowingTypeEnum);
        return true;
    }
}
