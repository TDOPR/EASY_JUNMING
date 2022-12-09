package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.model.EvmWithdraw;
import com.haoliang.model.condition.AppUserWithdrawCondition;
import com.haoliang.model.dto.AuditCheckDTO;
import com.haoliang.model.dto.UsdtWithdrawalDTO;
import com.haoliang.model.dto.WalletDTO;


public interface EvmWithdrawService extends IService<EvmWithdraw> {

    JsonResult pageList(PageParam<EvmWithdraw, AppUserWithdrawCondition> pageParam);

    JsonResult check(AuditCheckDTO auditCheckDTO);


    JsonResult usdtWithdrawal(UsdtWithdrawalDTO userWalletsDTO);

    /**
     * 提现到法币账户
     * @param walletDTO 提现明细
     */
    JsonResult fiatWithdrawal(WalletDTO walletDTO);
}
