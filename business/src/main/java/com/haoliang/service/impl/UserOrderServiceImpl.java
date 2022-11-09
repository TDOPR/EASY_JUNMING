package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.ProfitLogsMapper;
import com.haoliang.mapper.UserOrderDao;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.UserOrderEntity;
import com.haoliang.service.ProfitLogsService;
import com.haoliang.service.UserOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class UserOrderServiceImpl extends ServiceImpl<UserOrderDao, UserOrderEntity> implements UserOrderService {

}
