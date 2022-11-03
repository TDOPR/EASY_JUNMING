package com.haoliang.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.model.SysRole;
import com.haoliang.model.bo.SysRoleBO;
import com.haoliang.service.SysMenuService;
import com.haoliang.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理
 * @author Dominick Li
 **/
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 分页查询
     */
    @PostMapping("/pagelist")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public JsonResult queryByCondition(@RequestBody PageParam<SysRole> pageParam) {
        return roleService.queryByCondition(pageParam);
    }

    /**
     * 批量删除
     * @param idList 角色Id数组
     */
    @OperationLog(module = "角色管理", description = "批量删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:role:remove')")
    public JsonResult deleteByIds(@RequestBody String idList) {
        return JsonResult.build(roleService.removeByIds(JSONObject.parseArray(idList, Integer.class)));
    }

    /**
     * 新增或修改
     */
    @OperationLog(module = "角色管理", description = "新增或修改")
    @PostMapping("/")
    @PreAuthorize("hasAnyAuthority('sys:role:add','sys:role:edit')")
    public JsonResult save(@RequestBody SysRoleBO sysRoleBO) {
        return roleService.saveRole(sysRoleBO);
    }

    /**
     * 获取添加用户需要的下拉列表信息
     */
    @GetMapping("/getSelectList")
    public JsonResult getRoleAndChannel() {
        return roleService.findSelectList();
    }

    /**
     * 获取角色的菜单用于刷新
     */
    @GetMapping("/getMenuList")
    public JsonResult getMenuList(@RequestHeader String token) {
        return sysMenuService.findAllByToken(token);
    }

}
