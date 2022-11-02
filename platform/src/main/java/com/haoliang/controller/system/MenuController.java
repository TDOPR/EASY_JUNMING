package com.haoliang.controller.system;


import com.alibaba.fastjson.JSONObject;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.SysMenu;
import com.haoliang.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author Dominick Li
 * @description 菜单管理
 **/
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private SysMenuService sysMenuService;

    @OperationLog(module = "菜单管理", description = "添加或修改")
    @PostMapping("/saveMenu")
    public JsonResult saveMenu(@RequestBody SysMenu sysMenu) {
        return JsonResult.build(sysMenuService.saveOrUpdate(sysMenu));
    }

    @OperationLog(module = "菜单管理", description = "刷新菜单层级和顺序")
    @PostMapping("/reloadMenu")
    public JsonResult reloadMenu(@RequestBody List<SysMenu> sysMenus, @RequestHeader String token) {
        return sysMenuService.reloadMenu(sysMenus, token);
    }

    @OperationLog(module = "菜单管理", description = "批量删除")
    @PostMapping("/deleteByIds")
    public JsonResult deleteByIds(@RequestBody String idList) {
        return sysMenuService.deleteByIdList(JSONObject.parseArray(idList, Integer.class));
    }

    @GetMapping("/findAll")
    public JsonResult findAll() {
        return sysMenuService.findAll();
    }

}
