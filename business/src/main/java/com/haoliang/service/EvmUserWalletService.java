package com.haoliang.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.EvmUserWallet;

public interface EvmUserWalletService extends IService<EvmUserWallet> {

    EvmUserWallet getByAddress(String address, SFunction<EvmUserWallet, ?>... columns);

}
