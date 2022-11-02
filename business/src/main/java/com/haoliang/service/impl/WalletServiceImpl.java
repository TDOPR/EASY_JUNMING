package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.WalletMapper;
import com.haoliang.model.Wallets;
import com.haoliang.service.WalletService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.JwtTokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 12:20
 **/
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallets> implements WalletService {

    @Resource
    private WalletMapper walletMapper;

    @Override
    public JsonResult getMyWallet(String token) {
        Wallets wallets = this.getById(JwtTokenUtils.getUserIdFromToken(token));
        return JsonResult.successResult(wallets);
    }

}
