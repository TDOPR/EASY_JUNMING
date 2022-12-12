package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.DayRate;

import java.math.BigDecimal;
import java.time.LocalDate;


public interface DayRateService extends IService<DayRate> {

    /**
     * 生成今日的收益信息
     * @param localDate
     */
    DayRate insertDayRate(LocalDate localDate);

    /**
     * 获取今日的收益率行
     * @return 最新的收益
     */
    DayRate selectNewDayRate();

    /**
     * 查看指定日期的收益率行
     * @return 最新的收益
     */
    DayRate selectDayRateByLocalDate(LocalDate localDate);

    /**
     * 根据机器人等级获取今日收益
     * @param robotLevel 机器人等级
     * @return 日收益率
     */
    BigDecimal selectNewDayRate(Integer robotLevel);

    /**
     * 根据机器人等级获取这周收益
     * @param robotLevel 机器人等级
     * @return 周收益率
     */
    BigDecimal selectNewWeekRate(Integer robotLevel);

    /**
     * 根据机器人等级获取对应的收益
     * @param dayRate 收益信息对象
     * @param robotLevel 机器人等级
     * @return 机器人等级对应的收益率
     */
    BigDecimal getDayRateByLevel(DayRate dayRate,Integer robotLevel);


}
