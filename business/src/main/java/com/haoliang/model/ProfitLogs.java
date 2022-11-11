package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description 收益日结记录
 * @CreateTime 2022/11/1 17:17
 **/
@Data
@Builder
@TableName("profit_logs")
@NoArgsConstructor
@AllArgsConstructor
public class ProfitLogs extends BaseModel {


    /**
     * 所属用户的Id
     */
    private Integer userId;

    /**
     * 本金
     */
    private BigDecimal principal;

    /**
     * 收益率
     */
    private BigDecimal profitRate;

    /**
     * 产生的收益金额
     */
    private BigDecimal generatedAmount;

    /**
     * 是否发放给用户 1=已发放, 0=未发放
     */
    private Integer grantToUser;

}
