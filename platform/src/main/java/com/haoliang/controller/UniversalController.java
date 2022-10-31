package com.haoliang.controller;

import com.haoliang.model.meta.DeleteBO;
import com.haoliang.model.meta.SaveBO;
import com.haoliang.model.meta.UniversalQueryParamBO;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.service.UniversalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Dominick Li
 * @description 通用查询，修改,删除
 **/
@RestController
@RequestMapping("/universal")
public class UniversalController {

    @Autowired
    private UniversalService universalService;

    @PostMapping("/query")
    public JsonResult query(@RequestBody UniversalQueryParamBO universalQueryParam) {
        return universalService.query(universalQueryParam);
    }

    @PostMapping("/export")
    public void export(@RequestBody UniversalQueryParamBO universalQueryParam, HttpServletResponse response) {
         universalService.export(universalQueryParam,response);
    }

    @PostMapping("/")
    @OperationLog(module = "元数据通用",description = "添加或修改")
    public JsonResult save(@RequestBody SaveBO saveBO) {
        return universalService.save(saveBO);
    }

    @PostMapping("/delete")
    @OperationLog(module = "元数据通用组件",description = "删除")
    public JsonResult delete(@RequestBody DeleteBO deleteBO) {
        return universalService.delete(deleteBO);
    }

}
