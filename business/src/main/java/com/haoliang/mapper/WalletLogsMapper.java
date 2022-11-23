package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.dto.DateSection;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface WalletLogsMapper extends BaseMapper<WalletLogs> {
    List<WalletLogs> getMyProxyWalletLogs(@Param("userId")Integer userId,@Param("typeList")List<Integer> typeList);

    DateSection getDateSection(Integer userId);

    BigDecimal sumTotalAmountByType(Integer type);

}
