package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.PlatformTotalLockAmountDTO;

public interface WalletsMapper extends BaseMapper<Wallets> {

    PlatformTotalLockAmountDTO getPlatformTotalLockAmount();
}
