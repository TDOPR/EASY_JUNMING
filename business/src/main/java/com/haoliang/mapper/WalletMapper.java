package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.Wallets;

import java.math.BigDecimal;

public interface WalletMapper extends BaseMapper<Wallets> {
    BigDecimal findWalletAmountByUserId(Integer userId);

    Wallets findIdAndWalletsById(Integer userId);
}
