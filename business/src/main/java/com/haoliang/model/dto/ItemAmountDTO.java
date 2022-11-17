package com.haoliang.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/15 11:31
 **/
@Data
@AllArgsConstructor
public class ItemAmountDTO {

    /**
     * 团队总业绩
     */
    private BigDecimal itemIncome;

    /**
     * 下表
     */
    private Integer index;

    /**
     * 用户ID
     */
    private List<Integer> userIdList;
}
