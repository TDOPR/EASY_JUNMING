package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.config.GlobalConfig;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.SysLoginLog;
import com.haoliang.common.service.SysLoginLogService;
import com.haoliang.common.utils.IdUtils;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.common.utils.RedisUtils;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.model.AppUsers;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/4 11:21
 **/
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUsers> implements AppUserService {

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Override
    public JsonResult login(AppUserLoginDTO appUserLogin, String localIp) {
        String cacheKey = CacheKeyPrefixConstants.CAPTCHA_CODE + appUserLogin.getUuid();
        String code = RedisUtils.getCacheObject(cacheKey);
        if (code == null) {
            return JsonResult.failureResult("验证码已过期!");
        }
        if (!code.equals(appUserLogin.getCode())) {
            return JsonResult.failureResult("验证码错误!");
        }
        AppUsers appUsers;
        if (appUserLogin.getType() == 1) {
            appUsers = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getEamil, appUserLogin.getUsername()));
            if (appUsers == null) {
                //自动注册
                appUsers = new AppUsers();
                appUsers.setEamil(appUserLogin.getUsername());
                appUsers.setCode(getInviteCode());
                appUsers.setCode(code);
            } else {
                appUsers.setLoginCount(appUsers.getLoginCount() + 1);
            }

        } else {
            // TODO 手机号待确定供应商API
            appUsers=new AppUsers();
        }
        String token = JwtTokenUtils.getToken(appUsers.getId());
        RedisUtils.setCacheObject(CacheKeyPrefixConstants.APP_USER_TOKEN + appUsers.getId(), token, Duration.ofSeconds(GlobalConfig.getTokenExpire()));
        sysLoginLogService.save(new SysLoginLog(appUsers.getEamil(), localIp,2));
        return JsonResult.successResult();
    }

    /**
     * 生成唯一邀请码
     */
    private String getInviteCode() {
        String inviteCode = IdUtils.generateShortUUID(8);
        AppUsers exists;
        while (true) {
            exists = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getCode, inviteCode));
            if (exists != null) {
                inviteCode = IdUtils.generateShortUUID(8);
            } else {
                break;
            }
        }
        return inviteCode;
    }
}
