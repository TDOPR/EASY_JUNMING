package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.AddressPoolEntity;
import com.haoliang.model.EvmEventEntity;


public interface AddressPoolService extends IService<AddressPoolEntity> {
    String getAddress();

    void deleteByAddress(String address);
}
