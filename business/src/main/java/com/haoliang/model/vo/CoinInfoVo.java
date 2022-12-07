package com.haoliang.model.vo;

import com.haoliang.enums.CoinUnitEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Dominick Li
 * @Description 充值币种
 * @CreateTime 2022/12/6 17:22
 **/
@Data
public class CoinInfoVo {

    /**
     * 区块链地址
     */
    private List<BlockAddressVo> blockAddressList;

    /**
     * 法币提现手续费
     */
    private BigDecimal free = CoinUnitEnum.FIAT.getInterestRate();

}
