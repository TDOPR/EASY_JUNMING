package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.config.SysSettingParam;
import com.haoliang.common.config.GlobalConfig;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.SysLoginLog;
import com.haoliang.common.service.SysLoginLogService;
import com.haoliang.common.utils.IdUtils;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.common.utils.StringUtil;
import com.haoliang.common.utils.redis.RedisUtils;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.model.AppUsers;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
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
        appUsers = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getEamil, appUserLogin.getEmail()));
        if (appUsers == null) {
            Integer inviteUserId = null;
            if (StringUtil.isNotEmpty(appUserLogin.getInviteCode())) {
                //根据邀请码找到对应的用户
                AppUsers inviteUser = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getInviteCode, appUserLogin.getInviteCode()));
                if (inviteUser == null) {
                    return JsonResult.failureResult("邀请码错误！");
                }
                inviteUserId = inviteUser.getId();
            }
            //自动注册
            appUsers = new AppUsers();
            appUsers.setEamil(appUserLogin.getEmail());
            //生成邀请码
            appUsers.setInviteCode(getInviteCode());
            //设置用户的邀请人ID
            appUsers.setParentId(inviteUserId);
        } else {
            appUsers.setLoginCount(appUsers.getLoginCount() + 1);
        }

        String token = JwtTokenUtils.getToken(appUsers.getId());
        //单点登录需要删除用户在其它地方登录的Token
        if (SysSettingParam.isEnableSso()) {
            RedisUtils.deleteObjects(CacheKeyPrefixConstants.APP_TOKEN + appUsers.getId() + ":*");
        }
        RedisUtils.setCacheObject(CacheKeyPrefixConstants.APP_TOKEN + appUsers.getId(), token, Duration.ofSeconds(GlobalConfig.getTokenExpire()));
        sysLoginLogService.save(new SysLoginLog(appUsers.getEamil(), localIp, 2));
        return JsonResult.successResult();
    }

    /**
     * 生成唯一邀请码
     */
    private String getInviteCode() {
        String inviteCode = IdUtils.generateShortUUID(8);
        AppUsers exists;
        while (true) {
            exists = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getInviteCode, inviteCode));
            if (exists != null) {
                inviteCode = IdUtils.generateShortUUID(8);
            } else {
                break;
            }
        }
        return inviteCode;
    }
}
