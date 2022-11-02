package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.Wallets;
import com.haoliang.common.model.JsonResult;

public interface WalletService extends IService<Wallets> {

    JsonResult getMyWallet(String token);

}
