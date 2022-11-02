package com.haoliang.controller;

import com.alibaba.fastjson.JSONObject;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.model.MetaInfo;
import com.haoliang.service.MetaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dominick Li
 * @description 元数据管理
 **/
@RestController
@RequestMapping("/meta")
public class MetaController {

    @Autowired
    private MetaInfoService metaInfoService;

    /**
     * 分页查询的元数据列表
     */
    @PostMapping("/pagelist")
    public JsonResult findAll(@RequestBody PageParam<MetaInfo> pageParam) {
        return metaInfoService.findAll(pageParam);
    }

    @OperationLog(module = "元数据管理",description = "添加或修改")
    @PostMapping("/")
    public JsonResult save(@RequestBody @javax.validation.Valid MetaInfo metaInfo) {
        return metaInfoService.saveMetaInfo(metaInfo);
    }

    @OperationLog(module = "元数据管理",description = "删除")
    @PostMapping("/delete")
    public JsonResult delete(@RequestBody String idList) {
        return metaInfoService.deleteByIdList(JSONObject.parseArray(idList,Long.class));
    }

    /**
     * 获取数据库字段类型
     */
    @GetMapping("/getDataTypeList")
    public JsonResult getDataTypeList(){
        return metaInfoService.getDataTypeList();
    }

}
