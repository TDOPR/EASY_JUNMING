package com.haoliang.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Dominick Li
 * @Description 量化
 * @CreateTime 2022/11/15 16:53
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrusteeshipAmountVO {

    /**
     * 托管金额
     */
    private String amount;

    /**
     * 托管金额上限
     */
    private BigDecimal amountLimit;

    /**
     * 收益
     */
    private String profit;

    /**
     * 持仓收益率
     */
    private String profitRate;

    /**
     * 机器人等级
     */
    private Integer robotLevel;

    /**
     * 机器人的收益率
     */
    private BigDecimal robotRate;

    /**
     * 机器人做单策略
     */
    private List<StrategyVO>  strategyList;

}
