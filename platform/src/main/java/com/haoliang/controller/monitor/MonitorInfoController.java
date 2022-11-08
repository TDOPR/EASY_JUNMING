package com.haoliang.controller.monitor;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.MonitorInfoUtils;
import com.haoliang.model.vo.DataVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/8 14:37
 **/
@RestController
@RequestMapping("/monitor")
public class MonitorInfoController {

    /**
     * 服务器资源使用率信息
     */
    @GetMapping("/getMonitorInfo")
    @PreAuthorize("hasAuthority('sys:monitor:list')")
    public JsonResult getMonitorInfo(){
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
