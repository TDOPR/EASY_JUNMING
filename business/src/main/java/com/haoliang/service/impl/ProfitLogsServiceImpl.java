package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.ProfitLogsMapper;
import com.haoliang.model.ProfitLogs;
import com.haoliang.service.ProfitLogsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class ProfitLogsServiceImpl extends ServiceImpl<ProfitLogsMapper, ProfitLogs> implements ProfitLogsService {

    @Resource
    private ProfitLogsMapper profitLogsMapper;

    @Override
    public boolean updateUseByIdList(List<Long> idList) {
        return profitLogsMapper.updateUseByIdList(idList)==0;
    }
}
