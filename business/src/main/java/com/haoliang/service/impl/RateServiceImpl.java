package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.RateDao;
import com.haoliang.mapper.UserWithdrawDao;
import com.haoliang.model.RateEntity;
import com.haoliang.model.UserWithdrawEntity;
import com.haoliang.service.RateService;
import com.haoliang.service.UserWithdrawService;
import org.springframework.stereotype.Service;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service("UserWithdrawService")
public class RateServiceImpl extends ServiceImpl<RateDao, RateEntity> implements RateService {

}
