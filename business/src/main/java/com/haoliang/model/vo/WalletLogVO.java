package com.haoliang.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description 流水
 * @CreateTime 2022/11/14 11:00
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletLogVO {

    /**
     * 金额数值
     */
    @JsonIgnore
    private BigDecimal bigDecimalAmount;

    /**
     * 流水日期
     */
    private String createTime;

    /**
     * 流水类型名称
     */
    private String name;

    /**
     * 金额
     */
    private String amount;

    /**
     * 流水类型
     */
    @JsonIgnore
    private Integer flowingType;

    /**
     * 收支 1=收入 0=支出
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer type;
}
