package com.haoliang.service;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.MyTeamAmountDTO;
import com.haoliang.model.dto.UserWalletsDTO;
import com.haoliang.model.dto.WalletDTO;
import com.haoliang.model.vo.MyWalletsVO;

import java.math.BigDecimal;
import java.util.List;

public interface WalletsService extends IService<Wallets> {

    /**
     * 获取我的钱包信息
     * @return
     */
    JsonResult<MyWalletsVO> getMyWallet();

    /**
     * 充值
     * @param walletDTO 充值信息
     * @return
     */
    JsonResult recharge(WalletDTO walletDTO);

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
     * 更新钱包余额
     * @param blockAddress  区块链地址
     * @param amount  需要加或减的金额
     * @param flowingActionEnum 收入或支出
     * @param flowingTypeEnum 流水类型
     * @return 执行结果
     */
    boolean updateWallet(String blockAddress,BigDecimal amount, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum);

    /**
     * 根据直推用户获取我的团队业绩
     */
    MyTeamAmountDTO getMyItemAmount(List<Integer> firstUserIdList);

    /**
     * 根据用户Id获取我的团队业绩
     */
    MyTeamAmountDTO getMyItemAmountByUserId(Integer userId);

    /**
     * 获取平台总锁定金额   托管金额 + 机器人购买金额
     * @return
     */
    BigDecimal getPlatformTotalLockAmount();


    List<UserWalletsDTO> selectUserWalletsDTOListByUserLevelGtAndPrincipalAmountGe(int level, BigDecimal proxyMinMoney);

    /**
     * 通过数据库字段计算的方式修改钱包余额
     * @param userId 用户Id
     * @param amount 修改的金额
     * @param flowingActionEnum 增加或减少
     * @return
     */
    boolean lookUpdateWallets(Integer userId,BigDecimal amount,FlowingActionEnum flowingActionEnum);

    /**
     * 取消冻结用户金额
     * @param userId  用户Id
     * @param amount 需要解冻的金额
     * @return
     */
    boolean unFrozenAmount(Integer userId, BigDecimal amount);


    /**
     * 冻结金额
     * @param userId
     * @param amount
     * @return
     */
    boolean frozenAmount(Integer userId, BigDecimal amount);

    /**
     * 重冻结金额中扣除指定金额
     * @param userId 用户Id
     * @param amount 扣减的费用
     */
    boolean reduceFrozenAmount(Integer userId, BigDecimal amount);

    /**
     * 为用户绑定一条区块链地址
     * @param networdName 需要分配的网络名称
     * @return
     */
    JsonResult getBlockAddress(String networdName);
}
