package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.model.AppUserWithdraw;
import com.haoliang.model.Wallets;
import com.haoliang.model.condition.AppUserWithdrawCondition;
import com.haoliang.model.dto.AuditCheckDTO;


public interface AppUserWithdrawService extends IService<AppUserWithdraw> {

    JsonResult pageList(PageParam<AppUserWithdraw, AppUserWithdrawCondition> pageParam);

    JsonResult check(AuditCheckDTO auditCheckDTO);

    void withdraw(AppUserWithdraw appUserWithdraw, Wallets wallets);
}
