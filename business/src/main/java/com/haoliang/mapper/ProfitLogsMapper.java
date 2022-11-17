package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.ProfitLogs;

import java.math.BigDecimal;

public interface ProfitLogsMapper extends BaseMapper<ProfitLogs> {

    BigDecimal getYesterday(Integer userId);

    BigDecimal getLastWeek(Integer userId);

    BigDecimal getLastMonth(Integer userId);

    BigDecimal getTotal(Integer userId);
}
