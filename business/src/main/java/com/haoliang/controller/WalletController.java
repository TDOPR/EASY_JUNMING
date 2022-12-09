package com.haoliang.controller;

import com.haoliang.common.annotation.RepeatSubmit;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.dto.TypeDTO;
import com.haoliang.model.dto.BillDetailsDTO;
import com.haoliang.model.dto.UsdtWithdrawalDTO;
import com.haoliang.model.dto.WalletDTO;
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
    public JsonResult<MyWalletsVO> getMyWallet() {
        return walletsService.getMyWallet();
    }

    /**
     * 法币充值
     */
    @RepeatSubmit
    @PostMapping("/recharge")
    public JsonResult recharge(@Valid @RequestBody WalletDTO walletDTO) {
        return walletsService.recharge(walletDTO);
    }

    /**
     * usdt提现
     */
    @RepeatSubmit
    @PostMapping("/withdrawal/usdt")
    public JsonResult usdtWithdrawal(@Valid @RequestBody UsdtWithdrawalDTO userWalletsDTO) {
        return evmWithdrawService.usdtWithdrawal(userWalletsDTO);
    }

    /**
     * usdt提现
     */
    @RepeatSubmit
    @PostMapping("/withdrawal/fiat")
    public JsonResult fiatWithdrawal(@Valid @RequestBody WalletDTO WalletDTO) {
        return evmWithdrawService.fiatWithdrawal(WalletDTO);
    }

    /**
     * 账单明细
     */
    @PostMapping("/billDetails")
    public JsonResult<WalletLogsDetailVO>  billDetails(@RequestBody BillDetailsDTO billDetailsDTO){
        return walletLogService.getMybillDetails(billDetailsDTO);
    }

    /**
     * 量化收益明细
     */
    @PostMapping("/quantificationDetail")
    public JsonResult<ProfitLogsDetailVO> quantificationDetail(@RequestBody TypeDTO typeDTO){
        return walletLogService.quantificationDetail(typeDTO);
    }

    /**
     * 代理收益明细
     */
    @PostMapping("/proxyDetail")
    public JsonResult<ProxyWalletLogsDetailVO> proxyDetail(@RequestBody TypeDTO typeDTO){
        return walletLogService.proxyDetail(typeDTO);
    }

    /**
     * 为用户绑定一条区块链地址
     */
    @GetMapping("/getBlockAddress/{networdName}")
    public JsonResult getBlockAddress(@PathVariable String networdName){
        return walletsService.getBlockAddress(networdName);
    }
}
