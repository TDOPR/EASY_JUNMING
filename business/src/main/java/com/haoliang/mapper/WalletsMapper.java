package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.AppUsersAmountDTO;
import com.haoliang.model.dto.UserWalletsDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WalletsMapper extends BaseMapper<Wallets> {

    BigDecimal getPlatformTotalLockAmount();

    /**
     * 获取用户下级的所有业绩,但是不包含自己
     */
    BigDecimal getMyItemAmountByUserId(Integer inviteId);

    /**
     * 获取用户直推一代的业绩和用户Id
     * @param inviteId  邀请人Id
     */
    List<AppUsersAmountDTO> findAchievementByInviteId(Integer inviteId);


    /**
     * 查询大于指定等级的供应商信息
     * @param level 代理商等级
     * @param principalAmount 获取动态收益最低托管金额
     * @return
     */
    List<UserWalletsDTO> selectUserWalletsDTOListByUserLevelGtAndPrincipalAmountGe(@Param("level") Integer level,@Param("principalAmount") BigDecimal principalAmount);

    /**
     * 往钱包中充值
     * @param userId 用户Id
     * @param amount 充值的金额
     * @return
     */
    int lockUpdateAddWallet(@Param("userId")Integer userId,@Param("amount") BigDecimal amount);

    /**
     * 往钱包中取出金额
     * @param userId 用户Id
     * @param amount 取出的金额
     * @return
     */
    int lockUpdateReduceWallet(@Param("userId")Integer userId,@Param("amount") BigDecimal amount);
}
