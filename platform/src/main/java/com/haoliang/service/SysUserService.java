package com.haoliang.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.model.SysUser;
import com.haoliang.model.bo.LoginBO;
import com.haoliang.model.bo.UpdatePasswordBO;
import com.haoliang.model.bo.UpdateUserStatus;
import com.haoliang.model.condition.SysUserCondition;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


public interface SysUserService extends IService<SysUser> {

    /**
     * 登录认证
     */
    JsonResult login(LoginBO loginBO, String clientIp);


    /**
     * 启用或者禁用用户
     */
    JsonResult userEnabled(UpdateUserStatus updateUserStatus);

    /**
     * 修改密码
     */
    JsonResult updatePassword(UpdatePasswordBO updatePasswordBO);



    JsonResult restPassword(Integer id);

    /**
     * 按条件导出用户数据
     */
    void exportUsers(PageParam<SysUser, SysUserCondition> pageParam, HttpServletResponse response);

    JsonResult saveUser(SysUser sysUser);

    JsonResult deleteByIdList(List<Integer> idList);


    JsonResult queryByCondition(PageParam<SysUser,SysUserCondition> pageParam);

}
