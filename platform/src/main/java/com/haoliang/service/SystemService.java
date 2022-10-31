package com.haoliang.service;


import com.haoliang.common.model.JsonResult;

public interface SystemService {

    JsonResult getSetting();

    JsonResult getMonitorInfo();
}
