package com.haoliang.controller.system;


import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.model.SysMenu;
import com.haoliang.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 菜单管理
 * @author Dominick Li
 **/
@RestController
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 查询所有菜单
     */
    @GetMapping("/")
    public JsonResult findAll() {
        return sysMenuService.findAll();
    }

    /**
     * 添加或修改
     */
    @OperationLog(module = "菜单管理", description = "添加或修改")
    @PostMapping("/")
    public JsonResult saveMenu(@RequestBody SysMenu sysMenu) {
        return JsonResult.build(sysMenuService.saveOrUpdate(sysMenu));
    }

    /**
     * 刷新菜单层级和顺序
     */
    @OperationLog(module = "菜单管理", description = "刷新菜单层级和顺序")
    @PostMapping("/reloadMenu")
    public JsonResult reloadMenu(@RequestBody List<SysMenu> sysMenus, @RequestHeader(JwtTokenUtils.TOKEN_NAME) String token) {
        return sysMenuService.reloadMenu(sysMenus, token);
    }

    /**
     * 删除
     * @param id 菜单Id
     */
    @OperationLog(module = "菜单管理", description = "批量删除")
    @DeleteMapping("/{id}")
    public JsonResult deleteById(@PathVariable Integer id) {
        return sysMenuService.deleteById(id);
    }



}