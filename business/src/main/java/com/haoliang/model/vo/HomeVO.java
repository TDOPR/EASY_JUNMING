package com.haoliang.model.vo;

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
    private Integer address;

    /**
     * 总地址
     */
    private Integer totalAddress;

    /**
     * 我的托管量
     */
    private String trusteeshipAmount;

    /**
     * 总托管量
     */
    private String totalTrusteeshipAmount;

    /**
     * 周收益率
     */
    private String weekRate;

    /**
     * 日收益率
     */
    private String dayRate;

    /**
     * 周收益率 区间
     */
    private String weekRateSection;

    /**
     * 日收益率 区间
     */
    private String dayRateSection;


}
