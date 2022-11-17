package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.DayRate;

import java.math.BigDecimal;


public interface DayRateService extends IService<DayRate> {

    DayRate selectNewDayRate();

    BigDecimal selectNewDayRate(Integer robotLevel);

    BigDecimal selectNewWeekRate(Integer robotLevel);

    BigDecimal getDayRateByLevel(DayRate dayRate,Integer robotLevel);

}
