package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoliang.model.MetaInfo;

import java.util.Date;

public interface MetaInfoMapper extends BaseMapper<MetaInfo> {

    IPage<MetaInfo> selectbyPage(Page<MetaInfo> page, String metaName, Date beginDate, Date endDate);

}
