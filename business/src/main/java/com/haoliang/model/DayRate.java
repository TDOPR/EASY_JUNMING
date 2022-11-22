package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Administrator
 */
@Data
@Builder
@TableName("dayrate")
@NoArgsConstructor
@AllArgsConstructor
public class DayRate   {

    private static final long serialVersionUID = 1L;

    /**
     * 创建时间
     */
    private LocalDate createDate;

    /**
     * 未购买机器人利率
     */
    private BigDecimal level0;

    /**
     * 购买一级机器人利率
     */
    private BigDecimal level1;

    /**
     * 购买二级机器人利率
     */
    private BigDecimal level2;

    /**
     * 购买三级机器人利率
     */
    private BigDecimal level3;

    /**
     * 购买四级机器人利率
     */
    private BigDecimal level4;

    /**
     * 购买五级机器人利率
     */
    private BigDecimal level5;

}
