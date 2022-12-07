package com.haoliang.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class WalletOrderDTO {

    /**
     * 使用的货币类型   1=usdt 2=法币
     *
     * @required
     */
    @NotNull
    private Integer coinId;

    /**
     * 金额
     *
     * @required
     */
    @NotNull
    private BigDecimal amount;

    /**
     * 区块链地址  USDT提现的时候需要
     */
    private String address;

    /**
     * 提现网络类型
     */
    private String networdName;

}
