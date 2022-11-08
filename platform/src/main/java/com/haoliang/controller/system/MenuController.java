package com.haoliang.controller.system;


import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.bo.IntIdListBO;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.model.SysMenu;
import com.haoliang.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @GetMapping
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public JsonResult findAll() {
        return sysMenuService.findAll();
    }

    /**
     * 添加或修改
     */
    @OperationLog(module = "菜单管理", description = "添加或修改")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('sys:menu:add','sys:menu:edit')")
    public JsonResult saveMenu(@RequestBody SysMenu sysMenu) {
        return sysMenuService.saveMenu(sysMenu);
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
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:menu:remove')")
    public JsonResult deleteById(@RequestBody IntIdListBO intIdListBO) {
        return sysMenuService.deleteByIdList(intIdListBO.getIdList());
    }



}
