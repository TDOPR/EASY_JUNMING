package com.haoliang.controller.system;


import com.haoliang.annotation.OperationLog;
import com.haoliang.common.annotation.PrintLog;
import com.haoliang.common.constant.OperationAction;
import com.haoliang.common.constant.OperationModel;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.model.dto.IntIdListDTO;
import com.haoliang.common.model.dto.UpdatePasswordDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.util.IpAddrUtil;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.model.SysUser;
import com.haoliang.model.condition.SysUserCondition;
import com.haoliang.model.dto.LoginDTO;
import com.haoliang.model.dto.UpdateUserStatusDTO;
import com.haoliang.model.vo.AppTokenVO;
import com.haoliang.model.vo.RouterVO;
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
 *
 * @author Dominick Li
 **/
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 分页查询
     */
    @PostMapping("/pagelist")
    @PreAuthorize("hasAuthority('sys:user:list')")
    public JsonResult<PageVO<SysUser>> queryByCondition(@RequestBody PageParam<SysUser, SysUserCondition> pageParam) {
        if (pageParam.getSearchParam() == null) {
            pageParam.setSearchParam(new SysUserCondition());
        }
        return sysUserService.queryByCondition(pageParam);
    }

    /**
     * 导出用户信息
     *
     * @return 文件二进制流
     */
    @PostMapping("/export")
    @PreAuthorize("hasAuthority('sys:user:export')")
    public void exportUsers(@RequestBody PageParam<SysUser, SysUserCondition> pageParam, HttpServletResponse response) {
        sysUserService.exportUsers(pageParam, response);
    }

    /**
     * 启用或禁用用户
     */
    @OperationLog(module = OperationModel.SYS_USER, description = OperationAction.ENABLED_OR_DIABLED)
    @PostMapping("/userEnabled")
    @PreAuthorize("hasAuthority('sys:user:enabled')")
    public JsonResult userEnabled(@RequestBody UpdateUserStatusDTO updateUserStatusDTO) {
        return sysUserService.userEnabled(updateUserStatusDTO);
    }

    /**
     * 批量删除用户
     *
     * @param idList id数组
     */
    @OperationLog(module = OperationModel.SYS_USER, description = OperationAction.REMOVE)
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:user:remove')")
    public JsonResult deleteUsersByIds(@RequestBody IntIdListDTO intIdListDTO) {
        return sysUserService.deleteByIdList(intIdListDTO.getIdList());
    }

    /**
     * 添加或修改用户
     */
    @OperationLog(module = OperationModel.SYS_USER, description = OperationAction.ADD_OR_EDIT)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('sys:user:add','sys:user:edit')")
    public JsonResult save(@RequestBody SysUser sysUser) {
        return sysUserService.saveUser(sysUser);
    }

    /**
     * 重置密码
     */
    @OperationLog(module = OperationModel.SYS_USER, description = OperationAction.ENABLED_OR_DIABLED)
    @GetMapping("/restPassword/{id}")
    @PreAuthorize("hasAuthority('sys:user:resetPwd')")
    public JsonResult restPassword(@PathVariable Integer id) {
        return sysUserService.restPassword(id);
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    public JsonResult updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return sysUserService.updatePassword(updatePasswordDTO);
    }

    /**
     * 用户登录
     */
    @PrintLog
    @PostMapping("/login")
    public JsonResult<AppTokenVO> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        return sysUserService.login(loginDTO, IpAddrUtil.getLocalIp(request));
    }

    /**
     * 获取用户的菜单权限
     */
    @GetMapping("/getRouters")
    public JsonResult<RouterVO> getRouters() {
        return sysMenuService.findAllByRoleCode(JwtTokenUtil.getRoleCodeFromToken(ThreadLocalManager.getToken()));
    }

    /**
     * 生成用户google认证器的二维码图片
     */
    @GetMapping("/generateGoogleQRCode/{id}")
    public void generateGoogleQRCode(@PathVariable Integer userId, HttpServletResponse response) throws Exception {
        sysUserService.generateGoogleQRCode(userId, response);
    }
}
