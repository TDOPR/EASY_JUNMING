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
@TableName("eth_token_withdraw")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWithdrawEntity implements Serializable  {
    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId
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
     * 币种名称
     */
    private String coinName;
    /**
     * 币种类型
     */
    private String coinType;
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 交易id
     */
    private String txid;
    /**
     * 提现量(包含手续费)
     */
    private BigDecimal num;
    /**
     * 手续费
     */
    private BigDecimal fee;
    /**
     * 实际提现量
     */
    private BigDecimal mum;
    /**
     * 链上手续费花费
     */
    private BigDecimal chainFee;
    /**
     * 区块高度
     */
    private Integer blockNum;
    /**
     * 备注
     */
    private String remark;
    /**
     * 当前审核级数
     */
    private Integer step;
    /**
     * 状态：0-审核中;1-成功;2-拒绝;3-撤销;4-审核通过;5-打币中;6;-待区块确认;7-区块打币失败
     */
    private Integer status;
    /**
     * 审核时间
     */
    private Date auditTime;
    /**
     * 修改时间
     */
    private Date lastUpdateTime;
    /**
     * 创建时间
     */
    private Date created;
}
