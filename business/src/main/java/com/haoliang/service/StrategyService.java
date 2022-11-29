package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.AppUserRecharge;
import com.haoliang.model.DayRate;
import com.haoliang.model.Strategy;

import java.time.LocalDate;
import java.util.List;


public interface StrategyService extends IService<Strategy> {

    /**
     * 生成今日的机器人做单策略
     * @param localDate
     */
    List<Strategy> insertStrategy(LocalDate localDate);

    /**
     * 根据机器人等级获取需要显示的机器人做单策略信息
     * @param robotLevel 机器人等级
     */
    List<Strategy> getStrategyListByRobotLevel(Integer robotLevel);

}
