package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.ProfitLogsMapper;
import com.haoliang.mapper.UserWithdrawDao;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.UserWithdrawEntity;
import com.haoliang.service.ProfitLogsService;
import com.haoliang.service.UserWithdrawService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service("UserWithdrawService")
public class UserWithdrawServiceImpl extends ServiceImpl<UserWithdrawDao, UserWithdrawEntity> implements UserWithdrawService {

}
