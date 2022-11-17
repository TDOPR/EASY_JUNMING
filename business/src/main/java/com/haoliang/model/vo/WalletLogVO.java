package com.haoliang.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
     * 金额
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
     * 流水类型
     */
    @JsonIgnore
    private Integer type;

    /**
     * 金额字符串
     */
    private String amount;
}
