package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@TableName("address_pool")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressPoolEntity implements Serializable  {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String address;
//    private String txHash;
//    private String eventName;
//    private Date createTime;
}
