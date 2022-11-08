package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.IpAddrUtil;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * app端用户接口
 * @author Dominick Li
 * @CreateTime 2022/11/4 10:19
 **/
@RestController
@RequestMapping("/appuser")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    /**
     * 登录(自动注册)
     */
    @PostMapping("/login")
    public JsonResult login(@Valid @RequestBody AppUserLoginDTO appUserLogin, HttpServletRequest request){
        return appUserService.login(appUserLogin, IpAddrUtil.getLocalIp(request));
    }

}
