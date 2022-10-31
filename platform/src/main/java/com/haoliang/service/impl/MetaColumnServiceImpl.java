package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.MetaColumnMapper;
import com.haoliang.model.MetaColumn;
import com.haoliang.service.MetaColumnService;
import org.springframework.stereotype.Service;

@Service
public class MetaColumnServiceImpl extends ServiceImpl<MetaColumnMapper, MetaColumn> implements MetaColumnService {
}
