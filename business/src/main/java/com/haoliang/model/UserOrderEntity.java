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
@TableName("user_order")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOrderEntity implements Serializable  {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 用户ID
     */
    private Integer uid;
    /**
     * 地址
     */
    private String address;
    /**
     * 订单类型
     */
    private Integer type;
    /**
     * 订单金额
     */
    private BigDecimal amount;
    /**
     * 创建时间
     */
    private Date createTime;
}
