package com.haoliang.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.AmountDTO;
import com.haoliang.model.dto.RebotDTO;
import com.haoliang.model.dto.WalletOrderDTO;

import java.math.BigDecimal;

public interface WalletService extends IService<Wallets> {

    JsonResult getMyWallet(String token);

    JsonResult recharge(WalletOrderDTO walletOrderDTO, String token);

    JsonResult withdrawal(WalletOrderDTO walletOrderDTO, String token);

    JsonResult trusteeshipWithdrawal(AmountDTO amountDTO, String token);

    JsonResult trusteeshipRecharge(AmountDTO amountDTO, String token);

    JsonResult buyRebot(RebotDTO rebotDTO, String token);

    JsonResult upgradeRebot(RebotDTO rebotDTO, String token);

    Wallets selectColumnsByUserId(Integer userId, SFunction<Wallets, ?>... columns);

    /**
     * 更新钱包余额
     * @param amount  需要加或减的金额
     * @param userId  用户Id
     * @param flowingActionEnum 收入或支出
     * @param flowingTypeEnum 流水类型
     * @return 执行结果
     */
    boolean updateWallet(BigDecimal amount, Integer userId, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum);

}
