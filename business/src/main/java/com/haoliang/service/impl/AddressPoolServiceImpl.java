package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.AddressPoolDao;
import com.haoliang.mapper.EvmEventDao;
import com.haoliang.model.AddressPoolEntity;
import com.haoliang.model.EvmEventEntity;
import com.haoliang.service.AddressPoolService;
import com.haoliang.service.EvmEventService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class AddressPoolServiceImpl extends ServiceImpl<AddressPoolDao, AddressPoolEntity> implements AddressPoolService {

    @Resource
    AddressPoolDao addressPoolDao;

    public String getAddress(){
        return addressPoolDao.getAddress();
    }

    @Override
    public void deleteByAddress(String address) {
        addressPoolDao.deleteByAddress(address);
    }
}
