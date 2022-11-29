package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Dominick Li
 * @Description 量化
 * @CreateTime 2022/11/15 16:53
 **/
@Data
@Builder
@TableName("strategy")
@NoArgsConstructor
@AllArgsConstructor
public class Strategy {

    /**
     * 创建时间
     */
    @JsonIgnore
    private LocalDate createDate;

    /**
     * 序号   不同机器人级别显示不同的序号(每天生成5条策略) 1级显示1条, 2级显示2条
     */
    @JsonIgnore
    private Integer sortIndex;

    /**
     * 策略类型
     */
    private String strategyType;

    /**
     * 基准货币(QC)
     */
    private String qc;

    /**
     * 计价货币(BC)
     */
    private String bc;

    /**
     * 交叉货币对(CRO)
     */
    private String cro;

    /**
     * 派生交易量(DERN)
     */
    private String dern;

    /**
     * 量化指标TI
     */
    private String ti;

    /**
     * 生态基数EIPM
     */
    private String eipM;

    /**
     * 生态基数EIPN
     */
    private String eipN;

    /**
     * 生态基数EIP
     */
    private String eip;

}
