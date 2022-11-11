package com.haoliang.model.vo;

import com.haoliang.constant.EasyTradeConfig;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/10 12:28
 **/
@Data
public class HomeVO {

    /**
     * 24h新增地址
     */
    private String address;

    /**
     * 总地址
     */
    private String totalAddress;

    /**
     * 我的托管量
     */
    private String trusteeshipAmount;

    /**
     * 周收益率
     */
    private String week;

    /**
     * 日收益率
     */
    private String day= EasyTradeConfig.PROFIT_RATE.toString();


}
