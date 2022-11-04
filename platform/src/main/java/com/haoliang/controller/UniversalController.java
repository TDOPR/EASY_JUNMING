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
 * 元数据通用CRUD接口
 * @author Dominick Li
 **/
@RestController
@RequestMapping("/universal")
public class UniversalController {

    @Autowired
    private UniversalService universalService;

    /**
     * 查询
     */
    @PostMapping("/query")
    public JsonResult query(@RequestBody UniversalQueryParamBO universalQueryParam) {
        return universalService.query(universalQueryParam);
    }

    /**
     * 导出
     */
    @PostMapping("/export")
    public void export(@RequestBody UniversalQueryParamBO universalQueryParam, HttpServletResponse response) {
         universalService.export(universalQueryParam,response);
    }

    /**
     * 添加或修改
     */
    @PostMapping("/")
    @OperationLog(module = "元数据通用",description = "添加或修改")
    public JsonResult save(@RequestBody SaveBO saveBO) {
        return universalService.save(saveBO);
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @OperationLog(module = "元数据通用组件",description = "删除")
    public JsonResult delete(@RequestBody DeleteBO deleteBO) {
        return universalService.delete(deleteBO);
    }

}
