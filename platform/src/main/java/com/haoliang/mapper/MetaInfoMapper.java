package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.model.MetaInfo;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

public interface MetaInfoMapper extends BaseMapper<MetaInfo> {

    IPage<MetaInfo> selectbyPage(IPage<MetaInfo> page, @Param("metaName") String  metaName, @Param("beginDate") LocalDateTime beginDate, @Param("endDate") LocalDateTime  endDate);

}
