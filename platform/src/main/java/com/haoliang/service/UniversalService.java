package com.haoliang.service;

import com.alibaba.fastjson.JSONObject;
import com.haoliang.model.meta.DeleteBO;
import com.haoliang.model.meta.SaveBO;
import com.haoliang.model.meta.UniversalQueryParamBO;
import com.haoliang.common.model.JsonResult;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Dominick Li
 * @description 通用模型服务
 **/
public interface UniversalService {

    JsonResult<JSONObject> query(UniversalQueryParamBO universalQueryParam);

    JsonResult save(SaveBO saveBO);

    JsonResult delete(DeleteBO deleteBO);

    void export(UniversalQueryParamBO universalQueryParam, HttpServletResponse response);
}
