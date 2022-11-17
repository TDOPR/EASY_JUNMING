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
    public BigDecimal selectNewDayRate(Integer robotLevel) {
        //获取今日的利率
        DayRate dayRate = this.getOne(new LambdaQueryWrapper<DayRate>().ge(DayRate::getCreateDate, LocalDate.now()));
        if (ObjectUtils.isEmpty(dayRate)) {
            //插入一条新数据
            dayRate = new DayRate();
            dayRate.setCreateDate(LocalDate.now());
            dayRate.setLevel0(RobotEnum.getProfitRateByLevel(RobotEnum.ZERO.getLevel()));
            dayRate.setLevel1(RobotEnum.getProfitRateByLevel(RobotEnum.ONE.getLevel()));
            dayRate.setLevel2(RobotEnum.getProfitRateByLevel(RobotEnum.TW0.getLevel()));
            dayRate.setLevel3(RobotEnum.getProfitRateByLevel(RobotEnum.THREE.getLevel()));
            dayRate.setLevel4(RobotEnum.getProfitRateByLevel(RobotEnum.FOUR.getLevel()));
            dayRate.setLevel5(RobotEnum.getProfitRateByLevel(RobotEnum.FIVE.getLevel()));
            this.save(dayRate);
        }
        return getDayRateByLevel(dayRate, robotLevel);
    }

    @Override
    public BigDecimal selectNewWeekRate(Integer robotLevel) {
        LocalDate date = LocalDate.now();
        date = date.minusDays(6);
        //获取最近一周收益率
        List<DayRate> dayRateList = this.list(new LambdaQueryWrapper<DayRate>().ge(DayRate::getCreateDate, date));
        //计算周收益
        BigDecimal sum;
        switch (robotLevel) {
            case 1:
                sum = dayRateList.stream().map(DayRate::getLevel1).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case 2:
                sum = dayRateList.stream().map(DayRate::getLevel2).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case 3:
                sum = dayRateList.stream().map(DayRate::getLevel3).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case 4:
                sum = dayRateList.stream().map(DayRate::getLevel4).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case 5:
                sum = dayRateList.stream().map(DayRate::getLevel5).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            default:
                sum = dayRateList.stream().map(DayRate::getLevel0).reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
        }
        return sum;
    }

    @Override
    public DayRate selectNewDayRate() {
        //获取今日的利率
        DayRate dayRate = this.getOne(new LambdaQueryWrapper<DayRate>().ge(DayRate::getCreateDate, LocalDate.now()));
        if (ObjectUtils.isEmpty(dayRate)) {
            //插入一条新数据
            dayRate = new DayRate();
            dayRate.setCreateDate(LocalDate.now());
            dayRate.setLevel0(RobotEnum.getProfitRateByLevel(RobotEnum.ZERO.getLevel()));
            dayRate.setLevel1(RobotEnum.getProfitRateByLevel(RobotEnum.ONE.getLevel()));
            dayRate.setLevel2(RobotEnum.getProfitRateByLevel(RobotEnum.TW0.getLevel()));
            dayRate.setLevel3(RobotEnum.getProfitRateByLevel(RobotEnum.THREE.getLevel()));
            dayRate.setLevel4(RobotEnum.getProfitRateByLevel(RobotEnum.FOUR.getLevel()));
            dayRate.setLevel5(RobotEnum.getProfitRateByLevel(RobotEnum.FIVE.getLevel()));
            this.save(dayRate);
        }
        return dayRate;
    }

    @Override
    public BigDecimal getDayRateByLevel(DayRate dayRate, Integer robotLevel) {
        switch (robotLevel) {
            case 1:
                return dayRate.getLevel1();
            case 2:
                return dayRate.getLevel2();
            case 3:
                return dayRate.getLevel3();
            case 4:
                return dayRate.getLevel4();
            case 5:
                return dayRate.getLevel5();
            default:
                return dayRate.getLevel0();
        }
    }
}
