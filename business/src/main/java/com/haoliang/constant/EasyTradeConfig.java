package com.haoliang.constant;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dominick Li
 * @Description 业务场景类
 * @CreateTime 2022/11/1 11:09
 **/
public class EasyTradeConfig {

    /**
     * 代理资格所需最低金额
     */
    public static final BigDecimal PROXY_MIN_MONEY=new BigDecimal(300);

    /**
     * 最低托管金额限制
     */
    public static final Integer MIN_AMOUNT=10;

    /**
     * 代数奖只拿几代
     */
    public static final List<Integer> ALGEBRA_LEVEL = Arrays.asList(1,2,3);

    /**
     * 特别奖分红比例
     */
    public static final BigDecimal SPECIAL_AWARD_RATE=new BigDecimal("0.05");

    /**
     * 法币和美元汇率   119:1 实际需要调用接口获取最新汇率
     */
    public static BigDecimal XPF_2_USD = new BigDecimal(119.38);

    /**
     * 泰达币和美元汇率   1:1
     */
    public static BigDecimal USDT_2_USD = new BigDecimal(1);

    /**
     * 超过多少美元提交需要审核
     */
    public static BigDecimal AMOUNT_CHECK = new BigDecimal(2000);


}
