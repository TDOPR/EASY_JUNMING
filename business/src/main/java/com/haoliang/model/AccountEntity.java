package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@TableName("account")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity implements Serializable  {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    private Integer uid;
    /**
     * 币种id
     */
    private Integer coinId;
    /**
     * 账号状态：1，正常；2，冻结；
     */
    private Integer status;
    /**
     * 币种可用金额
     */
    private BigDecimal alreadyRechargeAmount;
    /**
     * 币种冻结金额
     */
    private BigDecimal freezeAmount;
    /**
     * 待释放金额
     */
    private BigDecimal pendingAmount;
    /**
     * 累计充值金额
     */
    private BigDecimal rechargeAmount;
    /**
     * 超级用户静态收益
     */
    private BigDecimal sAmount;

    /**
     * 累计提现金额
     */
    private BigDecimal withdrawalsAmount;
    /**
     * 充值地址
     */
    private String recAddr;
    /**
     * bzt锁仓量
     */
    private BigDecimal netValue;
    /**
     * 占用保证金
     */
    private BigDecimal lockMargin;
    /**
     * 持仓盈亏/浮动盈亏
     */
    private BigDecimal floatProfit;
    /**
     * 总盈亏
     */
    private BigDecimal totalProfit;
    /**
     * 版本号
     */
    private Integer version;
    /**
     * 更新时间
     */
    private Date lastUpdateTime;
    /**
     * 创建时间
     */
    private Date created;
}
