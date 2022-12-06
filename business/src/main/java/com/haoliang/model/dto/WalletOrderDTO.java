package com.haoliang.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class WalletOrderDTO {

    /**
     * 使用的货币类型  1=法币 2=usdt
     *
     * @required
     */
    @NotNull
    private Integer coinUnit;

    /**
     * 金额
     *
     * @required
     */
    @NotNull
    private BigDecimal amount;


    /**
     * 区块链地址  提现的时候需要
     */
    private String blockAddress;

}
