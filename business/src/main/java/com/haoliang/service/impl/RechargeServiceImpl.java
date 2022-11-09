package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.RechargeDao;
import com.haoliang.model.RechargeEntity;
import com.haoliang.service.RechargeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service("RechargeService")
public class RechargeServiceImpl extends ServiceImpl<RechargeDao, RechargeEntity> implements RechargeService {

    @Resource
    private RechargeDao rechargeDao;

    public List<RechargeEntity> selectPatch(){
        return rechargeDao.selectPatch();
    }

    public void updateType(int uid){
        rechargeDao.updateType(uid);
    }
}
