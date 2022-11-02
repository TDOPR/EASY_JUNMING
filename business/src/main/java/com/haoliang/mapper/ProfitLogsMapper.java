package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.ProfitLogs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProfitLogsMapper extends BaseMapper<ProfitLogs> {

    int updateUseByIdList(@Param("idList")List<Long> idList);

}
