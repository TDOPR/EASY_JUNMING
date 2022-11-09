package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@TableName("eth_token_coin_config")
public class CoinConfigEntity implements Serializable  {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 币种
     */
    private String coin;
    /**
     * 币种ID
     */
    private Integer coinId;
    /**
     * 币种类型
     */
    private String coinType;
    /**
     * 合约ID
     */
    private String contract;
    /**
     * token 名称
     */
    private String token;
    /**
     * 小数位数
     */
    private Integer round;
    /**
     * 主钱包地址
     */
    private String mainAddress;
    /**
     * 主钱包密码
     */
    private String password;
    /**
     * 归集钱包地址
     */
    private String collectAddress;
    /**
     * 账户名
     */
    private String accountName;
    /**
     * 归集的区块高度
     */
    private BigInteger blockNo;
    /**
     * 是否可用：E可用，D不可用
     */
    private String valid;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
}
