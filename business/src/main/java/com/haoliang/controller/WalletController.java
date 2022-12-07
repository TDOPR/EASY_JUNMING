package com.haoliang.controller;

import com.haoliang.common.annotation.RepeatSubmit;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.dto.TypeDTO;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.model.dto.BillDetailsDTO;
import com.haoliang.model.dto.UsdtWithdrawalDTO;
import com.haoliang.model.dto.WalletOrderDTO;
import com.haoliang.model.vo.MyWalletsVO;
import com.haoliang.model.vo.ProfitLogsDetailVO;
import com.haoliang.model.vo.ProxyWalletLogsDetailVO;
import com.haoliang.model.vo.WalletLogsDetailVO;
import com.haoliang.service.EvmWithdrawService;
import com.haoliang.service.WalletLogsService;
import com.haoliang.service.WalletsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 钱包接口
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

    @Autowired
    private EvmWithdrawService evmWithdrawService;

    /**
     * 我的钱包
     */
    @GetMapping
    public JsonResult<MyWalletsVO> getMyWallet(@RequestHeader(JwtTokenUtil.TOKEN_NAME) String token) {
        return walletsService.getMyWallet(token);
    }

    /**
     * 充值
     */
    @RepeatSubmit
    @PostMapping("/recharge")
    public JsonResult recharge(@Valid @RequestBody WalletOrderDTO walletOrderDTO, @RequestHeader(JwtTokenUtil.TOKEN_NAME) String token) {
        return walletsService.recharge(walletOrderDTO, token);
    }

    /**
     * usdt提现
     */
    @RepeatSubmit
    @PostMapping("/withdrawal/usdt")
    public JsonResult withdrawal(@Valid @RequestBody UsdtWithdrawalDTO userWalletsDTO, @RequestHeader(JwtTokenUtil.TOKEN_NAME) String token) {
        return evmWithdrawService.usdtWithdrawal(userWalletsDTO,token);
    }

    /**
     * 提现
     */
//    @RepeatSubmit
//    @PostMapping("/withdrawal/fiat")
//    public JsonResult withdrawal(@Valid @RequestBody UsdtWithdrawalDTO userWalletsDTO, @RequestHeader(JwtTokenUtil.TOKEN_NAME) String token) {
//        return walletsService.usdtWithdrawal(userWalletsDTO,token);
//    }

    /**
     * 账单明细
     */
    @PostMapping("/billDetails")
    public JsonResult<WalletLogsDetailVO>  billDetails(@RequestHeader(JwtTokenUtil.TOKEN_NAME) String token, @RequestBody BillDetailsDTO billDetailsDTO){
        return walletLogService.getMybillDetails(token,billDetailsDTO);
    }

    /**
     * 量化收益明细
     */
    @PostMapping("/quantificationDetail")
    public JsonResult<ProfitLogsDetailVO> quantificationDetail(@RequestHeader(JwtTokenUtil.TOKEN_NAME) String token, @RequestBody TypeDTO typeDTO){
        return walletLogService.quantificationDetail(token, typeDTO);
    }

    /**
     * 代理收益明细
     */
    @PostMapping("/proxyDetail")
    public JsonResult<ProxyWalletLogsDetailVO> proxyDetail(@RequestHeader(JwtTokenUtil.TOKEN_NAME) String token,@RequestBody TypeDTO typeDTO){
        return walletLogService.proxyDetail(token,typeDTO);
    }
}
