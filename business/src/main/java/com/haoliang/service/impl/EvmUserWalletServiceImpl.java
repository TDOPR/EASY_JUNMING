package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.EvmUserWalletMapper;
import com.haoliang.model.EvmUserWallet;
import com.haoliang.service.EvmUserWalletService;
import org.springframework.stereotype.Service;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/12/6 17:14
 **/
@Service
public class EvmUserWalletServiceImpl extends ServiceImpl<EvmUserWalletMapper, EvmUserWallet> implements EvmUserWalletService {

    @Override
    public EvmUserWallet getByAddress(String address, SFunction<EvmUserWallet, ?>... columns) {
        return this.getOne(new LambdaQueryWrapper<EvmUserWallet>()
                .select(columns)
                .eq(EvmUserWallet::getAddress, address)
        );
    }
}
