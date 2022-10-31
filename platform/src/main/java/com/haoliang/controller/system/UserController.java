package com.haoliang.controller.system;


import com.alibaba.fastjson.JSONObject;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.annotation.PrintLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.utils.IpAddrUtil;
import com.haoliang.model.bo.LoginBO;
import com.haoliang.model.bo.UpdatePasswordBO;
import com.haoliang.model.bo.UpdateUserStatus;
import com.haoliang.model.SysUser;
import com.haoliang.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Dominick Li
 * @description 用户管理
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/queryByCondition")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult queryByCondition(@RequestBody PageParam<SysUser> pageParam) {
        return sysUserService.queryByCondition(pageParam);
    }

    @PostMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void exportUsers(@RequestBody  PageParam<SysUser> pageParam, HttpServletResponse response) {
        sysUserService.exportUsers(pageParam,response);
    }

    @OperationLog(module = "用户管理", description = "禁用或者启用")
    @PostMapping("/userEnabled")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult userEnabled(@RequestBody UpdateUserStatus updateUserStatus) {
        return sysUserService.userEnabled(updateUserStatus);
    }

    @OperationLog(module = "用户管理", description = "删除")
    @PostMapping("/deleteByIds")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult deleteUsersByIds(@RequestBody String idList) {
        return sysUserService.deleteByIdList(JSONObject.parseArray(idList, Integer.class));
    }

    @OperationLog(module = "用户管理", description = "添加或修改")
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult save(@RequestBody SysUser sysUser) {
        return sysUserService.saveUser(sysUser);
    }

    @OperationLog(module = "用户管理", description = "重置密码")
    @GetMapping("/restPassword/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult restPassword(@PathVariable Integer id) {
        return sysUserService.restPassword(id);
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    public JsonResult updatePassword(@RequestBody UpdatePasswordBO updatePasswordBO) {
        return sysUserService.updatePassword(updatePasswordBO);
    }

    /**
     * 登录
     */
    @PrintLog
    @PostMapping("/login")
    public JsonResult login(@Valid @RequestBody LoginBO loginBO, HttpServletRequest request) {
        return sysUserService.login(loginBO, IpAddrUtil.getLocalIp(request));
    }

}
