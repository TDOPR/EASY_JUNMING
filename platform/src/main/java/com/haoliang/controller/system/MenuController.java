package com.haoliang.controller.system;


import com.haoliang.annotation.OperationLog;
import com.haoliang.common.constant.OperationAction;
import com.haoliang.common.constant.OperationModel;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.bo.IntIdListBO;
import com.haoliang.model.SysMenu;
import com.haoliang.model.vo.MenuTreeVO;
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
     * 获取添加角色需要的菜单列表信息
     */
    @GetMapping("/getTree")
    @PreAuthorize("hasAuthority('sys:menu:list')")
    public JsonResult<List<MenuTreeVO>> get() {
        return sysMenuService.getTree();
    }

    /**
     * 添加或修改
     */
    @OperationLog(module = OperationModel.SYS_MENU, description = OperationAction.ADD_OR_EDIT)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('sys:menu:add','sys:menu:edit')")
    public JsonResult saveMenu(@RequestBody SysMenu sysMenu) {
        return sysMenuService.saveMenu(sysMenu);
    }


    /**
     * 删除
     * @param id 菜单Id
     */
    @OperationLog(module = OperationModel.SYS_MENU, description = OperationAction.REMOVE)
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:menu:remove')")
    public JsonResult deleteById(@RequestBody IntIdListBO intIdListBO) {
        return sysMenuService.deleteByIdList(intIdListBO.getIdList());
    }

    //    /**
//     * 刷新菜单层级和顺序
//     */
//    @OperationLog(module = OperationModel.SYS_MENU, description = OperationAction.RELOAD)
//    @PostMapping("/reloadMenu")
//    public JsonResult reloadMenu(@RequestBody List<SysMenu> sysMenus, @RequestHeader(JwtTokenUtils.TOKEN_NAME) String token) {
//        return sysMenuService.reloadMenu(sysMenus, token);
//    }


}
