package com.haoliang.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.MyItemAmountDTO;
import com.haoliang.model.dto.WalletOrderDTO;
import com.haoliang.model.vo.MyWalletsVO;

import java.math.BigDecimal;
import java.util.List;

public interface WalletsService extends IService<Wallets> {

    /**
     * 获取我的钱包信息
     * @param token 身份凭证
     * @return
     */
    JsonResult<MyWalletsVO> getMyWallet(String token);

    /**
     * 充值
     * @param walletOrderDTO 充值信息
     * @param token 身份凭证
     * @return
     */
    JsonResult recharge(WalletOrderDTO walletOrderDTO, String token);

    /**
     * 提现
     * @param walletOrderDTO 提现信息
     * @param token 身份凭证
     * @return
     */
    JsonResult withdrawal(WalletOrderDTO walletOrderDTO, String token);


    /**
     * 查询钱包对象
     * @param userId 根据用户Id查询
     * @param columns 需要查询的字段
     * @return
     */
    Wallets selectColumnsByUserId(Integer userId, SFunction<Wallets, ?>... columns);

    /**
     * 更新钱包余额
     * @param amount  需要加或减的金额
     * @param userId  用户Id
     * @param flowingActionEnum 收入或支出
     * @param flowingTypeEnum 流水类型
     * @return 执行结果
     */
    boolean updateWallet(BigDecimal amount, Integer userId, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum);

    /**
     * 根据直推用户获取我的团队业绩
     */
    MyItemAmountDTO getMyItemAmount(List<Integer> firstUserIdList);

    /**
     * 根据用户Id获取我的团队业绩
     */
    MyItemAmountDTO getMyItemAmountByUserId(Integer userId);

    /**
     * 获取平台总锁定金额   托管金额 + 机器人购买金额
     * @return
     */
    BigDecimal getPlatformTotalLockAmount();

}
