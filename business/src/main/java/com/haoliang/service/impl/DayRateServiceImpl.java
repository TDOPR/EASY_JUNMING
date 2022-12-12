package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.enums.RobotEnum;
import com.haoliang.mapper.DayRateMapper;
import com.haoliang.model.DayRate;
import com.haoliang.service.DayRateService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/15 14:28
 **/
@Service
public class DayRateServiceImpl extends ServiceImpl<DayRateMapper, DayRate> implements DayRateService {

    @Override
    public DayRate insertDayRate(LocalDate localDate) {
        DayRate dayRate = DayRate.builder()
                .createDate(localDate)
                .level0(RobotEnum.getProfitRateByLevel(RobotEnum.ZERO.getLevel()))
                .level1(RobotEnum.getProfitRateByLevel(RobotEnum.ONE.getLevel()))
                .level2(RobotEnum.getProfitRateByLevel(RobotEnum.TW0.getLevel()))
                .level3(RobotEnum.getProfitRateByLevel(RobotEnum.THREE.getLevel()))
                .level4(RobotEnum.getProfitRateByLevel(RobotEnum.FOUR.getLevel()))
                .level5(RobotEnum.getProfitRateByLevel(RobotEnum.FIVE.getLevel()))
                .build();
        //插入一条新数据
        this.save(dayRate);
        return dayRate;
    }


    @Override
    public BigDecimal selectNewDayRate(Integer robotLevel) {
        //获取今日的利率
        LocalDate now = LocalDate.now();
        DayRate dayRate = this.getOne(new LambdaQueryWrapper<DayRate>().eq(DayRate::getCreateDate, now));
        if (ObjectUtils.isEmpty(dayRate)) {
            dayRate = insertDayRate(now);
        }
        return getDayRateByLevel(dayRate, robotLevel);
    }


    @Override
    public BigDecimal selectNewWeekRate(Integer robotLevel) {
        LocalDate date = LocalDate.now();
        date = date.minusDays(6);
        //获取最近一周收益率
        List<DayRate> dayRateList;
        //计算周收益
        BigDecimal sum;
        RobotEnum robotEnum = RobotEnum.levelOf(robotLevel);
        switch (robotEnum) {
            case ONE:
                dayRateList = this.list(new LambdaQueryWrapper<DayRate>().select(DayRate::getLevel1).ge(DayRate::getCreateDate, date));
                sum = dayRateList.stream().map(DayRate::getLevel1).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case TW0:
                dayRateList = this.list(new LambdaQueryWrapper<DayRate>().select(DayRate::getLevel2).ge(DayRate::getCreateDate, date));
                sum = dayRateList.stream().map(DayRate::getLevel2).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case THREE:
                dayRateList = this.list(new LambdaQueryWrapper<DayRate>().select(DayRate::getLevel3).ge(DayRate::getCreateDate, date));
                sum = dayRateList.stream().map(DayRate::getLevel3).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case FOUR:
                dayRateList = this.list(new LambdaQueryWrapper<DayRate>().select(DayRate::getLevel4).ge(DayRate::getCreateDate, date));
                sum = dayRateList.stream().map(DayRate::getLevel4).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case FIVE:
                dayRateList = this.list(new LambdaQueryWrapper<DayRate>().select(DayRate::getLevel5).ge(DayRate::getCreateDate, date));
                sum = dayRateList.stream().map(DayRate::getLevel5).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            default:
                dayRateList = this.list(new LambdaQueryWrapper<DayRate>().select(DayRate::getLevel0).ge(DayRate::getCreateDate, date));
                sum = dayRateList.stream().map(DayRate::getLevel0).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
        }
        return sum;
    }

    @Override
    public DayRate selectNewDayRate() {
        //获取今日的利率
        LocalDate now = LocalDate.now();
        DayRate dayRate = this.getOne(new LambdaQueryWrapper<DayRate>().eq(DayRate::getCreateDate, now));
        if (ObjectUtils.isEmpty(dayRate)) {
            //插入一条新数据
            dayRate = this.insertDayRate(now);
        }
        return dayRate;
    }

    @Override
    public BigDecimal getDayRateByLevel(DayRate dayRate, Integer robotLevel) {
        RobotEnum robotEnum = RobotEnum.levelOf(robotLevel);
        switch (robotEnum) {
            case ONE:
                return dayRate.getLevel1();
            case TW0:
                return dayRate.getLevel2();
            case THREE:
                return dayRate.getLevel3();
            case FOUR:
                return dayRate.getLevel4();
            case FIVE:
                return dayRate.getLevel5();
            default:
                return dayRate.getLevel0();
        }
    }

    @Override
    public DayRate selectDayRateByLocalDate(LocalDate localDate) {
        return this.getOne(new LambdaQueryWrapper<DayRate>().eq(DayRate::getCreateDate, localDate));
    }
}
