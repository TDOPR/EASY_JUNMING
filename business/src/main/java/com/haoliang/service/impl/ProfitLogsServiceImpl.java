package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.utils.NumberUtils;
import com.haoliang.mapper.ProfitLogsMapper;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.vo.MyWalletsVO;
import com.haoliang.service.ProfitLogsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
        UpdateWrapper<ProfitLogs> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(ProfitLogs::getGrantToUser, 1)
                .in(ProfitLogs::getId, idList);
        return this.update(wrapper);
    }

    @Override
    public MyWalletsVO.Quantification getMyQuantification(Integer userId) {
        BigDecimal yesterday = profitLogsMapper.getYesterday(userId);
        BigDecimal lastWeek = profitLogsMapper.getLastWeek(userId);
        BigDecimal lastMonth = profitLogsMapper.getLastMonth(userId);
        BigDecimal total = profitLogsMapper.getTotal(userId);
        return MyWalletsVO.Quantification.builder()
                .yesterday(NumberUtils.toMoeny(yesterday))
                .lastMonth(NumberUtils.toMoeny(lastMonth))
                .lastWeek(NumberUtils.toMoeny(lastWeek))
                .total(NumberUtils.toMoeny(total))
                .build();
    }
}
