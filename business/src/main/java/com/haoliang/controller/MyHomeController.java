package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.dto.UpdatePasswordDTO;
import com.haoliang.model.dto.AppUserDTO;
import com.haoliang.model.vo.MyDetailVO;
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
     * 获取用户和平台详细信息
     */
    @GetMapping
    public JsonResult<MyDetailVO> getMyDetail(){
        return appUserService.getMyDetail();
    }

    /**
     * 修改个人信息
     */
    @PostMapping("/modifyUserDetail")
    public JsonResult modifyUserDetail(@RequestBody AppUserDTO appUserDTO){
        return  appUserService.modifyUserDetail(appUserDTO);
    }

    /**
     * 上传头像
     */
    @PostMapping("/uploadHeadImage")
    public JsonResult uploadHeadImage(@RequestParam("file")MultipartFile file) throws Exception{
        return appUserService.uploadHeadImage(file);
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    public JsonResult updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return appUserService.updatePassword(updatePasswordDTO);
    }

}
