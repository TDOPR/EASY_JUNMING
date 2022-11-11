package com.haoliang.controller.system;

import com.haoliang.annotation.OperationLog;
import com.haoliang.common.constant.OperationAction;
import com.haoliang.common.constant.OperationModel;
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
     * 获取字典信息
     */
    @GetMapping("/setting")
    @PreAuthorize("hasAuthority('sys:system:list')")
    public JsonResult getSetting(){
        return systemService.getSetting();
    }

    /**
     * 修改字典信息
     */
    @OperationLog(module = OperationModel.SYS_SETTING,description = OperationAction.ADD_OR_EDIT)
    @PostMapping("/setting")
    @PreAuthorize("hasAuthority('sys:system:list')")
    public JsonResult saveSetting(@RequestBody Map<String, String> map){
        return sysDictionaryService.modifyBaseDictionary(map);
    }


}
