package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.AppUsers;
import com.haoliang.model.dto.AppUserLoginDTO;

public interface AppUserService extends IService<AppUsers> {

    JsonResult login(AppUserLoginDTO appUserLogin, String localIp);

}
