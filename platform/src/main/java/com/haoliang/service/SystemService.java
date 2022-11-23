package com.haoliang.service;


import com.haoliang.common.model.JsonResult;
import com.haoliang.model.vo.AdminHomeVO;

public interface SystemService {

    JsonResult getSetting();

    JsonResult getMonitorInfo();

    JsonResult<AdminHomeVO> getHomeInfo();
}
