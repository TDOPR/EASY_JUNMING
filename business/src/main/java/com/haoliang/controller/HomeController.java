package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.IpAddrUtil;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.model.dto.AppUserRegisterDTO;
import com.haoliang.model.dto.FindPasswordDTO;
import com.haoliang.model.vo.HomeVO;
import com.haoliang.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 首页
 * @author Dominick Li
 * @CreateTime 2022/11/8 17:34
 **/
@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private AppUserService appUserService;

    /**
     * 主页信息
     */
    @GetMapping("/")
    public JsonResult<HomeVO> home(){
        return appUserService.home();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public JsonResult register(@Valid @RequestBody AppUserRegisterDTO appUserRegisterDTO ) {
        return appUserService.register(appUserRegisterDTO);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public JsonResult login(@Valid @RequestBody AppUserLoginDTO appUserLogin, HttpServletRequest request) {
        return appUserService.login(appUserLogin, IpAddrUtil.getLocalIp(request));
    }

    /**
     * 找回密码
     */
    @PostMapping("/findPassword")
    public JsonResult findPassword(@Valid @RequestBody FindPasswordDTO findPasswordDTO){
        return appUserService.findPassword(findPasswordDTO);
    }



}
