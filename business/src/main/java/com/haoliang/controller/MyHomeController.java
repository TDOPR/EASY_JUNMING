package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.dto.UpdatePasswordDTO;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.model.dto.AppUserDTO;
import com.haoliang.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 我的主页
 * @author Dominick Li
 * @CreateTime 2022/11/18 11:04
 **/
@RestController
@RequestMapping("/myhome")
public class MyHomeController {

    @Autowired
    private AppUserService appUserService;

    /**
     * 修改个人信息
     */
    @PostMapping("/modifyUserDetail")
    public JsonResult modifyUserDetail(@RequestHeader(JwtTokenUtils.TOKEN_NAME)String token, @RequestBody AppUserDTO appUserDTO){
        return  appUserService.modifyUserDetail(token,appUserDTO);
    }

    /**
     * 上传头像
     */
    @PostMapping("/uploadHeadImage")
    public JsonResult uploadHeadImage(@RequestParam("file")MultipartFile file, @RequestHeader(JwtTokenUtils.TOKEN_NAME)String token) throws Exception{
        return appUserService.uploadHeadImage(token,file);
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    public JsonResult updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO, @RequestHeader(JwtTokenUtils.TOKEN_NAME)String token) {
        updatePasswordDTO.setUserId(JwtTokenUtils.getUserIdFromToken(token));
        return appUserService.updatePassword(updatePasswordDTO);
    }

}
