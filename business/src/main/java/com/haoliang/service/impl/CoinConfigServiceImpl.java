package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.CoinConfigDao;
import com.haoliang.mapper.ProfitLogsMapper;
import com.haoliang.model.CoinConfigEntity;
import com.haoliang.model.ProfitLogs;
import com.haoliang.service.CoinConfigService;
import com.haoliang.service.ProfitLogsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class CoinConfigServiceImpl extends ServiceImpl<CoinConfigDao, CoinConfigEntity> implements CoinConfigService {

}
