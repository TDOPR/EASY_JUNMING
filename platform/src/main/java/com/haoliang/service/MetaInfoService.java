package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.model.MetaInfo;

import java.util.List;

/**
 * @author Dominick Li
 * @description 元数据服务
 **/
public interface MetaInfoService extends IService<MetaInfo> {

    JsonResult getDataTypeList();

    JsonResult saveMetaInfo(MetaInfo metaInfo);

    JsonResult deleteByIdList(List<Long> parseArray);

    JsonResult findAll(PageParam<MetaInfo> pageParam);

    MetaInfo getMetaInfo(Long id);
}
