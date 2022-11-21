package com.haoliang.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.dto.UpdatePasswordDTO;
import com.haoliang.model.SysUser;
import com.haoliang.model.condition.SysUserCondition;
import com.haoliang.model.dto.LoginDTO;
import com.haoliang.model.dto.UpdateUserStatusDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface SysUserService extends IService<SysUser> {

    /**
     * 登录认证
     */
    JsonResult login(LoginDTO loginDTO, String clientIp);


    /**
     * 启用或者禁用用户
     */
    JsonResult userEnabled(UpdateUserStatusDTO updateUserStatusDTO);

    /**
     * 修改密码
     */
    JsonResult updatePassword(UpdatePasswordDTO updatePasswordDTO);



    JsonResult restPassword(Integer id);

    /**
     * 按条件导出用户数据
     */
    void exportUsers(PageParam<SysUser, SysUserCondition> pageParam, HttpServletResponse response);

    JsonResult saveUser(SysUser sysUser);

    JsonResult deleteByIdList(List<Integer> idList);


    JsonResult queryByCondition(PageParam<SysUser,SysUserCondition> pageParam);

    void generateGoogleQRCode(Integer userId, HttpServletResponse response) throws Exception;
}
