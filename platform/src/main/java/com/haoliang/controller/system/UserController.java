package com.haoliang.controller.system;


import com.alibaba.fastjson.JSONObject;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.annotation.PrintLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.utils.IpAddrUtil;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.model.SysUser;
import com.haoliang.model.bo.LoginBO;
import com.haoliang.model.bo.UpdatePasswordBO;
import com.haoliang.model.bo.UpdateUserStatus;
import com.haoliang.model.vo.RouterVO;
import com.haoliang.model.vo.TokenVO;
import com.haoliang.service.SysMenuService;
import com.haoliang.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * 系统用户管理
 * @author Dominick Li
 **/
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 分页查询
     */
    @PostMapping("/pagelist")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public JsonResult<PageVO<SysUser>> queryByCondition(@RequestBody PageParam<SysUser> pageParam) {
        return sysUserService.queryByCondition(pageParam);
    }

    /**
     * 导出用户信息
     * @return 文件二进制流
     */
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('sys:user:export')")
    public void exportUsers(@RequestBody  PageParam<SysUser> pageParam, HttpServletResponse response) {
        sysUserService.exportUsers(pageParam,response);
    }

    /**
     * 启用或禁用用户
     */
    @OperationLog(module = "用户管理", description = "禁用或者启用")
    @PostMapping("/userEnabled")
    public JsonResult userEnabled(@RequestBody UpdateUserStatus updateUserStatus) {
        return sysUserService.userEnabled(updateUserStatus);
    }

    /**
     * 批量删除用户
     * @param idList 用户id数组
     */
    @OperationLog(module = "用户管理", description = "删除")
    @PostMapping("/deleteByIds")
    @PreAuthorize("hasAuthority('sys:user:remove')")
    public JsonResult deleteUsersByIds(@RequestBody String idList) {
        return sysUserService.deleteByIdList(JSONObject.parseArray(idList, Integer.class));
    }

    /**
     * 添加或修改用户
     */
    @OperationLog(module = "用户管理", description = "添加或修改")
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('sys:user:add','sys:user:edit')")
    public JsonResult save(@RequestBody SysUser sysUser) {
        return sysUserService.saveUser(sysUser);
    }

    /**
     * 重置密码
     */
    @OperationLog(module = "用户管理", description = "重置密码")
    @GetMapping("/restPassword/{id}")
    @PreAuthorize("hasAuthority('sys:user:resetPwd')")
    public JsonResult restPassword(@PathVariable Integer id) {
        return sysUserService.restPassword(id);
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    public JsonResult updatePassword(@RequestBody UpdatePasswordBO updatePasswordBO,@RequestHeader(JwtTokenUtils.TOKEN_NAME)String token) {
        updatePasswordBO.setUserId(JwtTokenUtils.getUserIdFromToken(token));
        return sysUserService.updatePassword(updatePasswordBO);
    }

    /**
     * 用户登录
     */
    @PrintLog
    @PostMapping("/login")
    public JsonResult<TokenVO> login(@Valid @RequestBody LoginBO loginBO, HttpServletRequest request) {
        return sysUserService.login(loginBO, IpAddrUtil.getLocalIp(request));
    }

    /**
     * 获取用户的菜单权限
     */
    @GetMapping("/getRouters")
    public JsonResult<RouterVO> getRouters(@RequestHeader(JwtTokenUtils.TOKEN_NAME)String token){
        return sysMenuService.findAllByToken(token);
    }

}
