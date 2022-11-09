package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.enums.BusinessTypeEnum;
import com.haoliang.model.AccountDetailEntity;
import com.haoliang.model.ProfitLogs;

import java.math.BigDecimal;
import java.util.List;


public interface AccountDetailService extends IService<AccountDetailEntity> {
    void generateBill(int userId, BusinessTypeEnum businessTypeEnum, int orderId, BigDecimal amount);
}
