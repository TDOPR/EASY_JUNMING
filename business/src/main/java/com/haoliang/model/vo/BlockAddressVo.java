package com.haoliang.model.vo;

import com.haoliang.enums.CoinNetworkSourceEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/12/6 17:20
 **/
@Data
public class BlockAddressVo {

    /**
     * 网络名称
     */
    private String networdName;

    /**
     * 地址
     */
    private String address;

    /**
     * 提现手续费
     */
    private BigDecimal free;

    /**
     * 最低金额
     */
    private BigDecimal minAmount;

    public BlockAddressVo(String address, CoinNetworkSourceEnum coinNetworkSourceEnum) {
        this.networdName = coinNetworkSourceEnum.getName();
        this.address = address == null ? "" : address;
        this.free = coinNetworkSourceEnum.getFree();
        this.minAmount = coinNetworkSourceEnum.getMinAmount();
    }
}
