package com.haoliang.model.vo;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description 量化
 * @CreateTime 2022/11/15 16:53
 **/
@Data
public class TrusteeshipAmountVO {

    /**
     * 托管金额
     */
    private String amount;

    /**
     * 收益
     */
    private String profit;

    /**
     * 收益率
     */
    private String profitRate;

    /**
     * 机器人名称
     */
    private String robotName;
}
