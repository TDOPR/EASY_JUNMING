package com.haoliang.constant;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description 业务场景类
 * @CreateTime 2022/11/1 11:09
 **/
public interface EasyTradeConfig {

    /**
     * 代理资格所需最低金额
     */
    BigDecimal PROXY_MIN_MONEY=new BigDecimal(300);

    /**
     * 日量化浮动收益0.5%-1.2%  收益率每日随机更新
     */
    BigDecimal PROFIT_RATE = new BigDecimal(0.012);

}
