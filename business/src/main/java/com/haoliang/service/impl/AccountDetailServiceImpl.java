package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.enums.BusinessTypeEnum;
import com.haoliang.enums.CoinEnum;
import com.haoliang.mapper.AccountDetailDao;
import com.haoliang.mapper.ProfitLogsMapper;
import com.haoliang.model.AccountDetailEntity;
import com.haoliang.model.ProfitLogs;
import com.haoliang.service.AccountDetailService;
import com.haoliang.service.ProfitLogsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class AccountDetailServiceImpl extends ServiceImpl<AccountDetailDao, AccountDetailEntity> implements AccountDetailService {

    @Override
    public void generateBill(int userId, BusinessTypeEnum businessTypeEnum, int orderId, BigDecimal amount) {
        //流水记录
        AccountDetailEntity accountDetailEntity = new AccountDetailEntity();
        //默认链上账单，accountId为99,coin目前默认都收U
        accountDetailEntity.setAccountId(Integer.parseInt("99"));
        accountDetailEntity.setBusinessType(businessTypeEnum.getCode());
        accountDetailEntity.setUid(Integer.parseInt(userId + ""));
        accountDetailEntity.setCreated(new Date());
        accountDetailEntity.setCoinId((int) CoinEnum.RECHARGE_USDT.getCoinId());
        accountDetailEntity.setRemark(businessTypeEnum.getDesc());
        accountDetailEntity.setOrderId(orderId);
        accountDetailEntity.setDirection(2);
        accountDetailEntity.setRefAccountId(99);
        accountDetailEntity.setAmount(amount);
        baseMapper.insert(accountDetailEntity);
    }
}
