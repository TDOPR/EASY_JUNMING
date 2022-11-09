package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.RechargeEntity;
import com.haoliang.model.UserWithdrawEntity;

import java.util.List;
import java.util.Map;


public interface RechargeService extends IService<RechargeEntity> {
    List<RechargeEntity> selectPatch();

    void updateType(int uid);
}
