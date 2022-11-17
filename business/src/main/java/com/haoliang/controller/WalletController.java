package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.model.dto.BillDetailsDTO;
import com.haoliang.model.dto.WalletOrderDTO;
import com.haoliang.model.vo.MyWalletsVO;
import com.haoliang.service.WalletLogsService;
import com.haoliang.service.WalletsService;
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
    private WalletsService walletsService;

    @Autowired
    private WalletLogsService walletLogService;

    /**
     * 我的钱包
     */
    @GetMapping
    public JsonResult<MyWalletsVO> getMyWallet(@RequestHeader(JwtTokenUtils.TOKEN_NAME) String token) {
        return walletsService.getMyWallet(token);
    }

    /**
     * 钱包充值
     */
    @PostMapping("/recharge")
    public JsonResult recharge(@Valid @RequestBody WalletOrderDTO walletOrderDTO, @RequestHeader(JwtTokenUtils.TOKEN_NAME) String token) {
        return walletsService.recharge(walletOrderDTO, token);
    }

    /**
     * 钱包提现
     */
    @PostMapping("/withdrawal")
    public JsonResult withdrawal(@Valid @RequestBody WalletOrderDTO walletOrderDTO,@RequestHeader(JwtTokenUtils.TOKEN_NAME) String token) {
        return walletsService.withdrawal(walletOrderDTO,token);
    }

    /**
     * 账单明细
     */
    @PostMapping("/billDetails")
    public JsonResult  billDetails(@RequestHeader(JwtTokenUtils.TOKEN_NAME) String token,@RequestBody BillDetailsDTO billDetailsDTO){
        return walletLogService.getMybillDetails(token,billDetailsDTO);
    }
}
