package com.haoliang.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.config.AppParam;
import com.haoliang.common.config.GlobalConfig;
import com.haoliang.common.config.SysSettingParam;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.SysLoginLog;
import com.haoliang.common.model.dto.UpdatePasswordDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.service.SysLoginLogService;
import com.haoliang.common.utils.*;
import com.haoliang.common.utils.redis.RedisUtils;
import com.haoliang.enums.RobotEnum;
import com.haoliang.mapper.AddressPoolMapper;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.mapper.AppVersionsMapper;
import com.haoliang.model.AppUsers;
import com.haoliang.model.AppVersions;
import com.haoliang.model.TreePath;
import com.haoliang.model.Wallets;
import com.haoliang.model.condition.AppUsersCondition;
import com.haoliang.model.dto.AppUserDTO;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.model.dto.AppUserRegisterDTO;
import com.haoliang.model.dto.FindPasswordDTO;
import com.haoliang.model.vo.AppTokenVO;
import com.haoliang.model.vo.AppUsersVO;
import com.haoliang.model.vo.HomeVO;
import com.haoliang.service.AppUserService;
import com.haoliang.service.DayRateService;
import com.haoliang.service.TreePathService;
import com.haoliang.service.WalletsService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/4 11:21
 **/
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUsers> implements AppUserService {

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Autowired
    private WalletsService walletsService;

    @Autowired
    private DayRateService dayRateService;

    @Autowired
    private TreePathService treePathService;

    @Autowired
    private AppParam appParam;

    @Resource
    private AddressPoolMapper addressPoolMapper;

    @Resource
    private AppUserMapper appUserMapper;

    @Resource
    private AppVersionsMapper appVersionsMapper;


    @Override
    public JsonResult home(String token) {
        Integer robotLevel = 0;
        BigDecimal principalAmount = BigDecimal.ZERO;
        //判断当前用户是否登录
        if (!JwtTokenUtils.isTokenExpired(token)) {
            Integer userId = JwtTokenUtils.getUserIdFromToken(token);
            Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getPrincipalAmount, Wallets::getRobotLevel);
            robotLevel = wallets.getRobotLevel();
            principalAmount = wallets.getPrincipalAmount();
        }
        HomeVO homeVO = new HomeVO();
        RobotEnum robotEnum = RobotEnum.levelOf(robotLevel);
        homeVO.setDayRate(NumberUtils.formatBigDecimalToRateStr(dayRateService.selectNewDayRate(robotLevel)));
        homeVO.setWeekRate(NumberUtils.formatBigDecimalToRateStr(dayRateService.selectNewWeekRate(robotLevel)));
        homeVO.setDayRateSection(robotEnum.getDayRateSection());
        homeVO.setWeekRateSection(robotEnum.getWeekRateSection());
        //总地址
        homeVO.setTotalAddress((int) this.count());
        //24小时新增地址
        LocalDateTime dateTime = LocalDateTime.now();
        dateTime.minusDays(1);
        homeVO.setAddress((int) this.count(new LambdaQueryWrapper<AppUsers>().ge(AppUsers::getCreateTime, dateTime)));
        //我的托管量和平台托管基恩
        homeVO.setTrusteeshipAmount(NumberUtils.toMoeny(principalAmount));
        BigDecimal totalAmount = walletsService.getPlatformTotalLockAmount();
        homeVO.setTotalTrusteeshipAmount(NumberUtils.toMoeny(totalAmount));
        //获取主页显示的折线图数据 TODO

        return JsonResult.successResult(homeVO);
    }

    @Override
    public JsonResult<AppTokenVO> login(AppUserLoginDTO appUserLoginDTO, String localIp) {
        AppUsers appUsers = this.getOne(new LambdaQueryWrapper<AppUsers>().eq(AppUsers::getEmail, appUserLoginDTO.getEmail()));
        if (appUsers == null) {
            return JsonResult.failureResult(ReturnMessageEnum.EMAIL_NOT_EXISTS);
        } else if (appUsers.getEnabled() == 1) {
            return JsonResult.failureResult(ReturnMessageEnum.ACCOUNT_DISABLED);
        } else if (!appUsers.getPassword().equals(AESUtil.encrypt(appUserLoginDTO.getPassword(), appUsers.getSalt()))) {
            return JsonResult.failureResult(ReturnMessageEnum.PASSWORD_ERROR);
        }

        //修改登录次数
        UpdateWrapper<AppUsers> updateWrapper = Wrappers.update();
        updateWrapper.lambda()
                .set(AppUsers::getLoginCount, appUsers.getLoginCount() + 1)
                .eq(AppUsers::getId, appUsers.getId());
        this.update(updateWrapper);

        String token = JwtTokenUtils.getToken(appUsers.getId());

        //单点登录需要删除用户在其它地方登录的Token
        if (SysSettingParam.isEnableSso()) {
            RedisUtils.deleteObjects(CacheKeyPrefixConstants.APP_TOKEN + appUsers.getId() + ":*");
        }
        AppVersions appVersions = appVersionsMapper.selectOne(new LambdaQueryWrapper<AppVersions>().eq(AppVersions::getSystemName, appUserLoginDTO.getSystemName()));
        //把token存储到缓存中
        String tokenKey = CacheKeyPrefixConstants.APP_TOKEN + appUsers.getId() + ":" + IdUtils.simpleUUID();
        RedisUtils.setCacheObject(tokenKey, token, Duration.ofSeconds(GlobalConfig.getTokenExpire()));
        sysLoginLogService.save(new SysLoginLog(appUsers.getEmail(), localIp, 2));

        //返回token给客户端
        AppTokenVO appTokenVO = new AppTokenVO();
        BeanUtils.copyProperties(appUsers, appTokenVO);
        appTokenVO.setToken(tokenKey);
        appTokenVO.setVersion(appVersions.getVersion());
        appTokenVO.setPlatformDesc(appVersions.getPlatformDesc());
        return JsonResult.successResult(appTokenVO);
    }

    @Override
    @Transactional
    public JsonResult register(AppUserRegisterDTO appUserRegisterDTO) {
        String cacheKey = CacheKeyPrefixConstants.CAPTCHA_CODE + appUserRegisterDTO.getUuid();
        String code = RedisUtils.getCacheObject(cacheKey);
        if (code == null) {
            return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_EXPIRE);
        }
        if (!code.equals(appUserRegisterDTO.getCode())) {
            return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_ERROR);
        }
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
            walletsService.save(wallets);
            //添加一条默认的treepath记录
            TreePath treePath = TreePath.builder()
                    .ancestor(appUsers.getId())
                    .descendant(appUsers.getId())
                    .level(1)
                    .build();
            treePathService.save(treePath);
            if (inviteUserId != null) {
                //保存上下级关系
                treePathService.insertTreePath(appUsers.getId(), inviteUserId);
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
        //查询下机数量
        for (AppUsersVO appUsersVO : iPage.getRecords()) {
            appUsersVO.setSubordinateNumber(treePathService.countByAncestor(appUsersVO.getId()) - 1);
        }
        return JsonResult.successResult(new PageVO<>(iPage.getTotal(), iPage.getPages(), iPage.getRecords()));
    }

    @Override
    public JsonResult modifyUserDetail(String token, AppUserDTO appUserDTO) {
        UpdateWrapper<AppUsers> updateWrapper = Wrappers.update();
        updateWrapper.lambda()
                .set(AppUsers::getNickName, appUserDTO.getNickName())
                .set(AppUsers::getAutograph, appUserDTO.getAutograph())
                .eq(AppUsers::getId, JwtTokenUtils.getUserIdFromToken(token));
        boolean flag = this.update(updateWrapper);
        return JsonResult.build(flag);
    }

    @Override
    public JsonResult uploadHeadImage(String token, MultipartFile file) throws Exception {
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);

        String suffix = FileUtil.getSuffix(file.getOriginalFilename());
        String fileName = userId + "." + suffix;
        String savePath = appParam.getImageSavePath();
        String url = GlobalConfig.getVirtualPathURL() + StringUtil.replace(appParam.getImageSavePath(), appParam.getRootPath(), "") + fileName;
        //复制文件流到本地文件
        FileUtils.copyInputStreamToFile(file.getInputStream(), new File(savePath, fileName));
        UpdateWrapper<AppUsers> updateWrapper = Wrappers.update();
        updateWrapper.lambda()
                .set(AppUsers::getHeadImage, url)
                .eq(AppUsers::getId, JwtTokenUtils.getUserIdFromToken(token));
        boolean flag = this.update(updateWrapper);

        if (flag) {
            JSONObject object = new JSONObject();
            object.put("url", url);
            return JsonResult.successResult(object);
        }
        return JsonResult.failureResult();
    }

    @Override
    public JsonResult updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        AppUsers sysUser = this.getOne(new LambdaQueryWrapper<AppUsers>().select(AppUsers::getSalt, AppUsers::getPassword).eq(AppUsers::getId, updatePasswordDTO.getUserId()));
        String oldPwd = AESUtil.encrypt(updatePasswordDTO.getOldPassword(), sysUser.getSalt());
        if (sysUser.getPassword().equals(oldPwd)) {
            UpdateWrapper<AppUsers> wrapper = Wrappers.update();
            wrapper.lambda()
                    .set(AppUsers::getPassword, AESUtil.encrypt(updatePasswordDTO.getPassword(), sysUser.getSalt()))
                    .eq(AppUsers::getId, updatePasswordDTO.getUserId());
            update(null, wrapper);
            return JsonResult.successResult();
        }
        return JsonResult.failureResult(ReturnMessageEnum.ORIGINAL_PASSWORD_ERROR);
    }
}
