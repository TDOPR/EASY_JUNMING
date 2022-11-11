package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.config.GlobalConfig;
import com.haoliang.common.config.SysSettingParam;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.SysLoginLog;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.service.SysLoginLogService;
import com.haoliang.common.utils.AESUtil;
import com.haoliang.common.utils.IdUtils;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.common.utils.StringUtil;
import com.haoliang.common.utils.redis.RedisUtils;
import com.haoliang.mapper.AddressPoolMapper;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.mapper.TreePathMapper;
import com.haoliang.model.AppUsers;
import com.haoliang.model.Wallets;
import com.haoliang.model.condition.AppUsersCondition;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.model.dto.AppUserRegisterDTO;
import com.haoliang.model.dto.FindPasswordDTO;
import com.haoliang.model.vo.AppUsersVO;
import com.haoliang.model.vo.HomeVO;
import com.haoliang.model.vo.TokenVO;
import com.haoliang.service.AppUserService;
import com.haoliang.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    @Resource
    private AddressPoolMapper addressPoolMapper;

    @Resource
    private WalletService walletService;

    @Resource
    private TreePathMapper treePathMapper;

    @Resource
    private AppUserMapper appUserMapper;

    @Override
    public JsonResult home() {
        //获取主页显示的数据 TODO
        return JsonResult.successResult(new HomeVO());
    }

    @Override
    public JsonResult login(AppUserLoginDTO appUserLoginDTO, String localIp) {
        AppUsers appUsers = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getEmail, appUserLoginDTO.getEmail()));
        if (appUsers == null) {
            return JsonResult.failureResult(ReturnMessageEnum.EMAIL_NOT_EXISTS);
        } else if (appUsers.getEnabled() == 1) {
            return JsonResult.failureResult(ReturnMessageEnum.ACCOUNT_DISABLED);
        } else if (!appUsers.getPassword().equals(AESUtil.encrypt(appUserLoginDTO.getPassword(), appUsers.getSalt()))) {
            return JsonResult.failureResult(ReturnMessageEnum.PASSWORD_ERROR);
        }
        appUsers.setLoginCount(appUsers.getLoginCount() + 1);
        String token = JwtTokenUtils.getToken(appUsers.getId());

        //单点登录需要删除用户在其它地方登录的Token
        if (SysSettingParam.isEnableSso()) {
            RedisUtils.deleteObjects(CacheKeyPrefixConstants.APP_TOKEN + appUsers.getId() + ":*");
        }

        //把token存储到缓存中
        String tokenKey=CacheKeyPrefixConstants.APP_TOKEN + appUsers.getId()+":"+IdUtils.simpleUUID();
        RedisUtils.setCacheObject(tokenKey, token, Duration.ofSeconds(GlobalConfig.getTokenExpire()));
        sysLoginLogService.save(new SysLoginLog(appUsers.getEmail(), localIp, 2));
        return JsonResult.successResult(new TokenVO(tokenKey));
    }

    @Override
    @Transactional
    public JsonResult register(AppUserRegisterDTO appUserRegisterDTO) {
//        String cacheKey = CacheKeyPrefixConstants.CAPTCHA_CODE + appUserRegisterDTO.getUuid();
//        String code = RedisUtils.getCacheObject(cacheKey);
//        if (code == null) {
//            return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_EXPIRE);
//        }
//        if (!code.equals(appUserRegisterDTO.getCode())) {
//            return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_ERROR);
//        }
        AppUsers appUsers;
        appUsers = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getEmail, appUserRegisterDTO.getEmail()));
        if (appUsers == null) {
            Integer inviteUserId = null;
            if (StringUtil.isNotEmpty(appUserRegisterDTO.getInviteCode())) {
                //根据邀请码找到对应的用户
                AppUsers inviteUser = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getInviteCode, appUserRegisterDTO.getInviteCode()));
                if (inviteUser == null) {
                    return JsonResult.failureResult(ReturnMessageEnum.INVITE_CODE_ERROR);
                }
                inviteUserId = inviteUser.getId();
            }
            //自动注册
            appUsers = new AppUsers();
            appUsers.setEmail(appUserRegisterDTO.getEmail());
            //生成邀请码
            appUsers.setInviteCode(getInviteCode());
            //设置用户的邀请人ID
            appUsers.setInviteId(inviteUserId);
            //设置密码加密用的盐
            appUsers.setSalt(IdUtils.simpleUUID());
            appUsers.setPassword(AESUtil.encrypt(appUserRegisterDTO.getPassword(), appUsers.getSalt()));
            this.save(appUsers);

            //创建一条钱包记录
            Wallets wallets = new Wallets();
            wallets.setUserId(appUsers.getId());
            String address = addressPoolMapper.getAddress();
            if (address != null) {
                wallets.setBlockAddress(address);
                addressPoolMapper.deleteByAddress(address);
            }
            walletService.save(wallets);

            if (inviteUserId != null) {
                //保存上下级关系
                treePathMapper.insertTreePath(appUsers.getId(),inviteUserId);
            }
            return JsonResult.successResult();
        } else {
            return JsonResult.failureResult(ReturnMessageEnum.EMAIL_EXISTS);
        }
    }

    @Override
    public JsonResult findPassword(FindPasswordDTO findPasswordDTO) {
        String cacheKey = CacheKeyPrefixConstants.CAPTCHA_CODE + findPasswordDTO.getUuid();
        String code = RedisUtils.getCacheObject(cacheKey);
        if (code == null) {
            return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_EXPIRE);
        }
        if (!code.equals(findPasswordDTO.getCode())) {
            return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_ERROR);
        }
        AppUsers appUsers;
        appUsers = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getEmail, findPasswordDTO.getEmail()));
        if (appUsers == null) {
            return JsonResult.failureResult(ReturnMessageEnum.EMAIL_NOT_EXISTS);
        }
        appUsers.setPassword(AESUtil.encrypt(findPasswordDTO.getPassword(), appUsers.getSalt()));
        this.saveOrUpdate(appUsers);
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

    @Override
    public JsonResult<PageVO<AppUsersVO>> pageList(PageParam<AppUsers, AppUsersCondition> pageParam) {
        IPage<AppUsersVO> iPage = appUserMapper.page(pageParam.getPage(), pageParam.getSearchParam());
        //TODO
        for (AppUsersVO appUsersVO : iPage.getRecords()) {

        }
        return JsonResult.successResult(new PageVO<>(iPage.getTotal(), iPage.getPages(),iPage.getRecords()));
    }
}
