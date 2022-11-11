package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.model.dto.AmountDTO;
import com.haoliang.model.dto.RebotDTO;
import com.haoliang.model.dto.WalletOrderDTO;
import com.haoliang.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 钱包接口
 *
 * @author Dominick Li
 * @CreateTime 2022/11/1 12:16
 **/
@RestController
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    /**
     * 我的钱包
     */
    @GetMapping
    public JsonResult getMyWallet(@RequestHeader(JwtTokenUtils.TOKEN_NAME) String token) {
        return walletService.getMyWallet(token);
    }

    /**
     * 钱包充值
     */
    @PostMapping("/recharge")
    public JsonResult recharge(@Valid @RequestBody WalletOrderDTO walletOrderDTO, @RequestHeader(JwtTokenUtils.TOKEN_NAME) String token) {
        return walletService.recharge(walletOrderDTO, token);
    }

    /**
     * 钱包提现
     */
    @PostMapping("/withdrawal")
    public JsonResult withdrawal(@Valid @RequestBody WalletOrderDTO walletOrderDTO,@RequestHeader(JwtTokenUtils.TOKEN_NAME) String token) {
        return walletService.withdrawal(walletOrderDTO,token);
    }

    /**
     * 托管金额充值
     */
    @PostMapping("/trusteeship_recharge")
    public JsonResult entrustWithdrawal(@Valid @RequestBody AmountDTO amountDTO, @RequestHeader(JwtTokenUtils.TOKEN_NAME)String token){
        return walletService.trusteeshipRecharge(amountDTO,token);
    }

    /**
     * 托管金额提现到钱包
     */
    @PostMapping("/trusteeship_withdrawal")
    public JsonResult trusteeshipWithdrawal(@Valid @RequestBody AmountDTO amountDTO,@RequestHeader(JwtTokenUtils.TOKEN_NAME)String token){
        return walletService.trusteeshipWithdrawal(amountDTO,token);
    }

    /**
     * 购买机器人
     */
    @PostMapping("/buyRebot")
    public JsonResult buyRebot(@RequestBody RebotDTO rebotDTO, @RequestHeader(JwtTokenUtils.TOKEN_NAME)String token){
        return walletService.buyRebot(rebotDTO,token);
    }

    /**
     * 升级机器人等级
     */
    @PostMapping("/upgradeRebot")
    public JsonResult upgradeRebot(@RequestBody RebotDTO rebotDTO, @RequestHeader(JwtTokenUtils.TOKEN_NAME)String token){
        return walletService.upgradeRebot(rebotDTO,token);
    }



}
