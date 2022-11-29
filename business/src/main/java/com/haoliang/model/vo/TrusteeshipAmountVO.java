package com.haoliang.model.vo;

import com.haoliang.model.Strategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /**
     * 机器人做单策略
     */
    private List<Strategy>  strategyList;
}
