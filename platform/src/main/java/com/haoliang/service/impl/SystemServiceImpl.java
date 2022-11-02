package com.haoliang.service.impl;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.MonitorInfoUtils;
import com.haoliang.config.DictionaryParam;
import com.haoliang.model.vo.DataVO;
import com.haoliang.service.SystemService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author Dominick Li
 * @CreateTime 2020/7/14 20:53
 * @description 获取数据统计信息
 **/
@Service
public class SystemServiceImpl implements SystemService {

    @Override
    public JsonResult getSetting() {
        HashMap result = new HashMap();
        result.put("dictionary", DictionaryParam.getDictionaryParam());
        return JsonResult.successResult(result);
    }

    @Override
    public JsonResult getMonitorInfo() {
        HashMap resMap = new HashMap(4);
        //获取系统性能信息
        List list = MonitorInfoUtils.getMontiorList();
        //CPU使用率
        String cpuShare = list.get(0).toString();
        resMap.put("cpu", new DataVO(cpuShare + "%", cpuShare));
        //硬盘使用情况
        List info = (List) list.get(1);
        String diskInfo = info.get(0).toString();
        String diskShare = info.get(1).toString();
        resMap.put("disk", new DataVO(diskInfo, diskShare));
        //内存使用情况
        info = (List) list.get(2);
        String memoryInfo = info.get(0).toString();
        String memoryShare = info.get(1).toString();
        resMap.put("memory", new DataVO(memoryInfo, memoryShare));
        //显存使用情况 服务器监控
        info = (List) list.get(3);
        String gpuInfo = info.get(0).toString();
        String gpuShare = info.get(1).toString();
        resMap.put("gpu", new DataVO(gpuInfo, gpuShare));
        return JsonResult.successResult(resMap);
    }
}