package com.haoliang.service;


import com.haoliang.model.Wallets;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * @author Dominick Li
 * @CreateTime 2021/7/22 13:45
 * @description 异步调用服务
 **/
public interface AsyncService {

    /**
     * 获取三代内团队奖
     * @param wallets 用户钱包
     * @param userAmount 所有用户日收益map集合
     */
    void grantItemPrizeToUser(Wallets wallets, HashMap<Integer, BigDecimal> userAmount, CountDownLatch countDownLatch);
}
