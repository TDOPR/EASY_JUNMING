package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.AppUserRecharge;
import com.haoliang.model.Strategy;


public interface StrategyService extends IService<Strategy> {
    JsonResult<Strategy> getStrategyType(String token);
}
