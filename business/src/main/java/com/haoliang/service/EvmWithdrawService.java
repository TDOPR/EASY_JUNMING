package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.model.EvmWithdraw;
import com.haoliang.model.condition.AppUserWithdrawCondition;
import com.haoliang.model.dto.AuditCheckDTO;
import com.haoliang.model.dto.UsdtWithdrawalDTO;


public interface EvmWithdrawService extends IService<EvmWithdraw> {

    JsonResult pageList(PageParam<EvmWithdraw, AppUserWithdrawCondition> pageParam);

    JsonResult check(AuditCheckDTO auditCheckDTO);

    /**
     * 提现逻辑
     */
    void withdraw(EvmWithdraw evmWithdraw);

    JsonResult usdtWithdrawal(UsdtWithdrawalDTO userWalletsDTO, String token);
}
