package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.WalletLogs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WalletLogsMapper extends BaseMapper<WalletLogs> {
    List<WalletLogs> getMyProxyWalletLogs(@Param("userId")Integer userId,@Param("typeList")List<Integer> typeList);
}
