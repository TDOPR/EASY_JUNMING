package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.ProfitLogs;

import java.util.List;


public interface ProfitLogsService extends IService<ProfitLogs> {
    boolean updateUseByIdList(List<Long> idList);
}
