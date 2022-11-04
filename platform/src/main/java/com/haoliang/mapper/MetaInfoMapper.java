package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoliang.model.MetaInfo;
import com.haoliang.model.condition.MetaInfoCondition;
import org.apache.ibatis.annotations.Param;

public interface MetaInfoMapper extends BaseMapper<MetaInfo> {

    IPage<MetaInfo> selectbyPage(Page<MetaInfo> page,@Param("meta") MetaInfoCondition metaInfoCondition);

}
