package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.SysChannel;

import java.util.List;

public interface SysChannelMapper extends BaseMapper<SysChannel> {

    List<SysChannel> findAllByParentIdIsNullOrderBySortIndex();

}
