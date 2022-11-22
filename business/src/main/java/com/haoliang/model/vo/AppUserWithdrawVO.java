package com.haoliang.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Dominick Li
 * @Description 审核列表数据
 * @CreateTime 2022/11/11 16:42
 **/
@Data
public class AppUserWithdrawVO {

    /**
     * 任务Id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 用户账号
     */
    private String email;

    /**
     * 提现币种类型
     */
    @JsonIgnore
    private Integer coinUnit;

    /**
     * 提现币种类型
     */
    private String coinUnitName;

    /**
     * 提现金额(包含手续费)
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 审核时间
     */
    private LocalDateTime auditTime;

    /**
     * 审核状态
     */
    private Integer auditStatus;

    /**
     * 审核状态名称
     */
    private String auditStatusName;
}
