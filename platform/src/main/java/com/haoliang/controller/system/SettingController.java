package com.haoliang.controller.system;

import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.service.SysDictionaryService;
import com.haoliang.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 系统设置
 * @author Dominick Li
 **/
@RestController
@RequestMapping("/admin")
public class SettingController {

    @Autowired
    private SystemService systemService;

    @Autowired
    private SysDictionaryService sysDictionaryService;


    /**
     * 服务器资源使用率信息
     */
    @GetMapping("/getMonitorInfo")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult getMonitorInfo(){
        return systemService.getMonitorInfo();
    }

    /**
     * 获取字典信息
     */
    @GetMapping("/setting")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult getSetting(){
        return systemService.getSetting();
    }

    /**
     * 修改字典信息
     */
    @OperationLog(module = "系统设置",description = "修改基础字典")
    @PostMapping("/setting")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult saveSetting(@RequestBody Map<String, String> map){
        return sysDictionaryService.modifyBaseDictionary(map);
    }


}
