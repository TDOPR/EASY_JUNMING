package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCIDNoModifyTime;
import lombok.Data;

/**
 * 地址池表
 */
@Data
@TableName("address_pool")
public class AddressPool extends BaseModelCIDNoModifyTime  {

    /**
     * 区块链地址
     */
    private String address;

}
