package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.config.GlobalConfig;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.utils.AESUtil;
import com.haoliang.common.utils.DateUtil;
import com.haoliang.common.utils.RedisUtils;
import com.haoliang.common.utils.excel.ExcelUtil;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.config.LoginConfig;
import com.haoliang.enums.RoleTypeEnum;
import com.haoliang.mapper.SysUserMapper;
import com.haoliang.model.SysLoginLog;
import com.haoliang.model.SysMenu;
import com.haoliang.model.SysUser;
import com.haoliang.model.bo.LoginBO;
import com.haoliang.model.bo.UpdatePasswordBO;
import com.haoliang.model.bo.UpdateUserStatus;
import com.haoliang.model.vo.ExportUserVO;
import com.haoliang.model.vo.TokenVO;
import com.haoliang.model.vo.UserVO;
import com.haoliang.service.SysLoginLogService;
import com.haoliang.service.SysRoleService;
import com.haoliang.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @author Dominick Li
 * @CreateTime 2020/3/17 10:25
 **/
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Resource
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private LoginConfig loginConfig;

    @Override
    public JsonResult login(LoginBO loginBO, String clientIp) {
        //校验验证码
        JsonResult jsonResult = checkCaptcha(loginBO);
        if (jsonResult.getCode() != HttpStatus.OK.value()) {
            return jsonResult;
        }
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, loginBO.getUsername()).eq(SysUser::getDeleted, 0));
        boolean loginFlag = true;
        if (sysUser == null) {
            return JsonResult.failureResult("帐号不存在!");
        } else if (!sysUser.getPassword().equals(AESUtil.encrypt(loginBO.getPassword(), sysUser.getSalt()))) {
            //验证错误次数,当在指定时间内达到指定次数则锁定用户
            loginFlag = false;
        } else if (sysUser.getEnabled() == 0) {
            return JsonResult.failureResult("用户已被注销了!");
        }

        jsonResult = checkFailCount(sysUser.getUsername(), loginFlag);
        if (!loginFlag) {
            return jsonResult;
        }

        String roleCode = RoleTypeEnum.valueOf(sysUser.getRoleId()).getName();
        String token= JwtTokenUtils.getToken(sysUser.getId(), roleCode.toUpperCase(), sysUser.getUsername());
        RedisUtils.setCacheObject(CacheKeyPrefixConstants.TOKEN+sysUser.getId(),token, Duration.ofSeconds(GlobalConfig.getTokenExpire()));
        sysLoginLogService.save(new SysLoginLog(sysUser.getUsername(), clientIp));

        List<SysMenu> sysMenuList = sysRoleService.getMenuListByRole(sysUser.getRoleId());
        return JsonResult.successResult(new TokenVO(token, sysUser, roleCode, sysMenuList));
    }

    @Override
    public JsonResult saveUser(SysUser sysUser) {
        SysUser exists = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()).eq(SysUser::getDeleted, 0));
        if (exists != null && !exists.getId().equals(sysUser.getId())) {
            return JsonResult.failureResult("用户名已存在!");
        }
        if (sysUser.getId() == null) {
            //为了防止原密码一样,加密后的密码也一样的问题,用登录账号拼接密码进行加密
            sysUser.setSalt(UUID.randomUUID().toString().replace("-", ""));
            sysUser.setPassword(AESUtil.encrypt(sysUser.getPassword(), sysUser.getSalt()));
            this.save(sysUser);
        } else {
            exists.setRoleId(sysUser.getRoleId());
            exists.setName(sysUser.getName());
            exists.setUsername(sysUser.getUsername());
            exists.setChannelId(sysUser.getChannelId());
            this.saveOrUpdate(sysUser);
        }
        return JsonResult.successResult();
    }

    @Override
    public JsonResult<PageVO<UserVO>> queryByCondition(PageParam<SysUser> pageParam) {
        Date endDate = pageParam.getEndDate();
        if (endDate != null) {
            endDate = DateUtil.getDateStrIncrement(endDate, 1, TimeUnit.DAYS);
        }
        IPage<UserVO> page = sysUserMapper.selectPageVo(pageParam.getPage(), pageParam.getLike().get("username"), pageParam.getBeginDate(), endDate);
        return JsonResult.successResult(new PageVO(page));
    }

    @Override
    public JsonResult userEnabled(UpdateUserStatus updateUserStatus) {
        UpdateWrapper<SysUser> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(SysUser::getEnabled, updateUserStatus.getEnabled())
                .eq(SysUser::getId, updateUserStatus.getId());
        update(null, wrapper);
        return JsonResult.successResult();
    }


    @Override
    public JsonResult deleteByIdList(List<Integer> idList) {
        UpdateWrapper<SysUser> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(SysUser::getDeleted, 1)
                .in(SysUser::getId, idList);
        update(null, wrapper);
        return JsonResult.successResult();
    }

    @Override
    public JsonResult updatePassword(UpdatePasswordBO updatePasswordBO) {
        SysUser sysUser = this.getById(updatePasswordBO.getUserId());
        String oldPwd = AESUtil.encrypt(updatePasswordBO.getOldPassword(), sysUser.getSalt());
        if (sysUser.getPassword().equals(oldPwd)) {
            sysUser.setPassword(AESUtil.encrypt(updatePasswordBO.getPassword(), sysUser.getSalt()));
            this.saveOrUpdate(sysUser);
            return JsonResult.successResult();
        }
        return JsonResult.failureResult("原密码错误!");
    }


    @Override
    public JsonResult restPassword(Integer id) {
        SysUser sysUser = this.getById(id);
        sysUser.setPassword(AESUtil.encrypt("123456", sysUser.getSalt()));
        this.saveOrUpdate(sysUser);
        return JsonResult.successResult();
    }


    /**
     * 校验验证码
     */
    private JsonResult checkCaptcha(LoginBO loginBO) {
        if (loginConfig.getCaptcha().isEnable()) {
            String cacheKey = CacheKeyPrefixConstants.CAPTCHA_CODE + loginBO.getUuid();
            String code = RedisUtils.getCacheObject(cacheKey);
            if (code == null) {
                return JsonResult.failureResult("验证码已过期!");
            } else if (!code.equals(loginBO.getCode())) {
                return JsonResult.failureResult("验证码错误!");
            }
        }
        return JsonResult.successResult();
    }

    /**
     * 密码锁定校验
     */
    private JsonResult checkFailCount(String username, boolean loginFlag) {
        if (loginConfig.isEnableErrorLock()) {
            String cacheKey = CacheKeyPrefixConstants.PWD_ERROR_COUNT + username;
            if (loginFlag) {
                RedisUtils.deleteObject(cacheKey);
            } else {
                // 获取用户登录错误次数
                Integer errorNumber = RedisUtils.getCacheObject(cacheKey);
                if (errorNumber != null && errorNumber.equals(loginConfig.getMaxErrorNumber())) {
                    return JsonResult.failureResult(String.format("账号已被锁定%s秒,请稍后重试!", loginConfig.getLockTime()));
                } else {
                    errorNumber = errorNumber == null ? 1 : errorNumber + 1;
                    RedisUtils.setCacheObject(cacheKey, errorNumber, Duration.ofSeconds(loginConfig.getLockTime()));
                }
            }
        }
        return JsonResult.failureResult("密码错误");
    }

    @Override
    public void exportUsers(PageParam<SysUser> pageParam, HttpServletResponse response) {
        JsonResult<PageVO<UserVO>> jsonResult = this.queryByCondition(pageParam);
        PageVO<UserVO> data = jsonResult.getData();
        List<ExportUserVO> exportUserVOList = new ArrayList<>(data.getContent().size());
        for (UserVO userVO : data.getContent()) {
            exportUserVOList.add(new ExportUserVO(userVO));
        }
        ExcelUtil.exportData(ExportUserVO.class, "用户信息", exportUserVOList, response);
    }
}
