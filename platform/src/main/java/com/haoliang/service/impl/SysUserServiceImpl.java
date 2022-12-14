package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.config.GlobalProperties;
import com.haoliang.common.config.SysSettingParam;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.enums.ContentTypeEnum;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.SysLoginLog;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.model.dto.UpdatePasswordDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.service.SysLoginLogService;
import com.haoliang.common.util.*;
import com.haoliang.common.util.encrypt.AESUtil;
import com.haoliang.common.util.encrypt.RSAUtil;
import com.haoliang.common.util.excel.ExcelUtil;
import com.haoliang.common.util.google.GoogleAuthenticatorUtil;
import com.haoliang.common.util.redis.RedisUtil;
import com.haoliang.config.LoginConfig;
import com.haoliang.mapper.SysRoleMapper;
import com.haoliang.mapper.SysUserMapper;
import com.haoliang.model.SysRole;
import com.haoliang.model.SysUser;
import com.haoliang.model.condition.SysUserCondition;
import com.haoliang.model.dto.LoginDTO;
import com.haoliang.model.dto.UpdateUserStatusDTO;
import com.haoliang.model.vo.ExportUserVO;
import com.haoliang.model.vo.TokenVO;
import com.haoliang.model.vo.UserVO;
import com.haoliang.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.haoliang.common.util.google.QRCodeUtil.generateQRCodeImg;


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
    private LoginConfig loginConfig;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Value("${webSocket.adminwsPath}")
    private String adminwsPath;

    @Override
    public JsonResult login(LoginDTO loginDTO, String clientIp) {

        //???????????????????????????
        if (isLock(loginDTO.getUsername())) {
            return JsonResult.failureResult(ReturnMessageEnum.ACCOUNT_LOCK.setAndToString(loginConfig.getLockTime()));
        }

        //?????????????????????
        JsonResult jsonResult = checkImageCaptcha(loginDTO);
        if (jsonResult.getCode() != HttpStatus.OK.value()) {
            return jsonResult;
        }
        //???????????????????????????????????????
        //String password = RSAUtil.decryptString(loginDTO.getPassword());
        String password = loginDTO.getPassword();
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, loginDTO.getUsername()).eq(SysUser::getDeleted, 0));
        //??????????????????
        boolean loginFlag = true;
        if (sysUser == null) {
            return JsonResult.failureResult(ReturnMessageEnum.ACCOUNT_NOT_EXISTS);
        } else if (!sysUser.getPassword().equals(AESUtil.encrypt(password, sysUser.getSalt()))) {
            loginFlag = false;
        } else if (sysUser.getEnabled() == 0) {
            return JsonResult.failureResult(ReturnMessageEnum.ACCOUNT_DISABLED);
        }

        //??????google?????????
        if (!checkGoogleCaptcha(loginDTO.getCode(), sysUser.getGoogleSecret())) {
            return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_ERROR);
        }

        //??????????????????,??????????????????????????????????????????????????????
        setFailCount(sysUser.getUsername(), loginFlag);
        if (!loginFlag) {
            //????????????
            return JsonResult.failureResult(ReturnMessageEnum.PASSWORD_ERROR);
        }

        String tokenKey = CacheKeyPrefixConstants.TOKEN + sysUser.getId() + ":" + IdUtil.simpleUUID();
        SysRole sysRole = sysRoleMapper.selectById(sysUser.getRoleId());
        String token = JwtTokenUtil.getToken(sysUser.getId(), sysUser.getUsername(), sysRole.getRoleCode(), sysRole.getId());

        //??????????????????????????????????????????????????????Token
        if (SysSettingParam.isEnableSso()) {
            RedisUtil.deleteObjects(CacheKeyPrefixConstants.TOKEN + sysUser.getId() + ":*");
        }
        RedisUtil.setCacheObject(tokenKey, token, Duration.ofSeconds(GlobalProperties.getTokenExpire()));
        sysLoginLogService.save(new SysLoginLog(sysUser.getUsername(), clientIp, 1));
        return JsonResult.successResult(new TokenVO(tokenKey, GlobalProperties.getWebSocketAddress() + adminwsPath));
    }

    @Override
    public JsonResult saveUser(SysUser sysUser) {
        SysUser exists = this.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, sysUser.getUsername()).eq(SysUser::getDeleted, 0));
        if (exists != null && !exists.getId().equals(sysUser.getId())) {
            return JsonResult.failureResult(ReturnMessageEnum.ACCOUNT_NOT_EXISTS);
        }
        if (sysUser.getId() == null) {
            //??????????????????????????????
            sysUser.setSalt(IdUtil.simpleUUID());
            sysUser.setGoogleSecret(GoogleAuthenticatorUtil.genSecret(sysUser.getUsername(), loginConfig.getCaptcha().getGoogleHost()));
            sysUser.setPassword(AESUtil.encrypt(sysUser.getPassword(), sysUser.getSalt()));
            this.save(sysUser);
        } else {
            UpdateWrapper<SysUser> wrapper = Wrappers.update();
            wrapper.lambda()
                    .set(SysUser::getRoleId, sysUser.getRoleId())
                    .set(SysUser::getName, sysUser.getName())
                    .set(SysUser::getMobile, sysUser.getMobile())
                    .set(SysUser::getEmail, sysUser.getEmail())
                    .set(SysUser::getEnabled, sysUser.getEnabled())
                    //.set(SysUser::getChannelId, sysUser.getChannelId())
                    .eq(SysUser::getId, sysUser.getId());
            update(wrapper);
        }
        return JsonResult.successResult();
    }

    @Override
    public JsonResult<PageVO<UserVO>> queryByCondition(PageParam<SysUser, SysUserCondition> pageParam) {
        LocalDateTime endDate = pageParam.getSearchParam().getEndDate();
        if (endDate != null) {
            endDate = DateUtil.getDateStrIncrement(endDate, 1, TimeUnit.DAYS);
            pageParam.getSearchParam().setEndDate(endDate);
        }
        IPage<UserVO> page = sysUserMapper.selectPageVo(pageParam.getPage(), pageParam.getSearchParam());
        return JsonResult.successResult(new PageVO(page));
    }

    @Override
    public JsonResult userEnabled(UpdateUserStatusDTO updateUserStatusDTO) {
        UpdateWrapper<SysUser> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(SysUser::getEnabled, updateUserStatusDTO.getEnabled())
                .eq(SysUser::getId, updateUserStatusDTO.getId());
        update(wrapper);
        return JsonResult.successResult();
    }

    @Override
    public JsonResult deleteByIdList(List<Integer> idList) {
        UpdateWrapper<SysUser> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(SysUser::getDeleted, 1)
                .in(SysUser::getId, idList);
        update(wrapper);
        return JsonResult.successResult();
    }

    @Override
    public JsonResult updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().select(SysUser::getSalt, SysUser::getPassword).eq(SysUser::getId, userId));
        String oldPwd = RSAUtil.decryptString(updatePasswordDTO.getOldPassword());
        oldPwd = AESUtil.encrypt(oldPwd, sysUser.getSalt());
        if (sysUser.getPassword().equals(oldPwd)) {
            String password = RSAUtil.decryptString(updatePasswordDTO.getPassword());
            UpdateWrapper<SysUser> wrapper = Wrappers.update();
            wrapper.lambda()
                    .set(SysUser::getPassword, AESUtil.encrypt(password, sysUser.getSalt()))
                    .eq(SysUser::getId, userId);
            update(wrapper);
            return JsonResult.successResult();
        }
        return JsonResult.failureResult(ReturnMessageEnum.ORIGINAL_PASSWORD_ERROR);
    }


    @Override
    public JsonResult restPassword(Integer id) {
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().select(SysUser::getSalt).eq(SysUser::getId, id));
        UpdateWrapper<SysUser> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(SysUser::getPassword, AESUtil.encrypt("123456", sysUser.getSalt()))
                .in(SysUser::getId, id);
        update(wrapper);
        return JsonResult.successResult();
    }


    /**
     * ?????????????????????
     */
    private JsonResult checkImageCaptcha(LoginDTO loginDTO) {
        if (loginConfig.getCaptcha().isEnable() && !loginConfig.getCaptcha().isGoogle()) {
            //?????????????????????
            String cacheKey = CacheKeyPrefixConstants.CAPTCHA_CODE + loginDTO.getUuid();
            String code = RedisUtil.getCacheObject(cacheKey);
            if (code == null) {
                return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_EXPIRE);
            } else if (!code.equals(loginDTO.getCode())) {
                return JsonResult.failureResult(ReturnMessageEnum.VERIFICATION_CODE_ERROR);
            }
        }
        return JsonResult.successResult();
    }

    /**
     * ??????google???????????????
     */
    private boolean checkGoogleCaptcha(String code, String googleSecret) {
        if (loginConfig.getCaptcha().isEnable() && loginConfig.getCaptcha().isGoogle()) {
            //????????????????????? ?????????
            if (StringUtil.isAnyBlank(code, googleSecret)) {
                return false;
            }
            return GoogleAuthenticatorUtil.authcode(code, googleSecret);
        }
        return true;
    }

    /**
     * ?????????????????????????????????????????????
     */
    private boolean isLock(String username) {
        if (loginConfig.isEnableErrorLock()) {
            String cacheKey = CacheKeyPrefixConstants.PWD_ERROR_COUNT + username;
            Integer errorNumber = RedisUtil.getCacheObject(cacheKey);
            if (errorNumber != null && errorNumber.equals(loginConfig.getMaxErrorNumber())) {
                return true;
            }
        }
        return false;
    }


    /**
     * ??????????????????
     */
    private void setFailCount(String username, boolean loginFlag) {
        if (loginConfig.isEnableErrorLock()) {
            String cacheKey = CacheKeyPrefixConstants.PWD_ERROR_COUNT + username;
            if (loginFlag) {
                // ??????????????????,?????????????????????
                RedisUtil.deleteObject(cacheKey);
            } else {
                // ??????????????????????????????,????????????1
                Integer errorNumber = RedisUtil.getCacheObject(cacheKey);
                errorNumber = errorNumber == null ? 1 : errorNumber + 1;
                RedisUtil.setCacheObject(cacheKey, errorNumber, Duration.ofSeconds(loginConfig.getLockTime()));
            }
        }
    }

    @Override
    public void exportUsers(PageParam<SysUser, SysUserCondition> pageParam, HttpServletResponse response) {
        //????????????????????????????????????
        pageParam.setPageSize(100000);
        JsonResult<PageVO<UserVO>> jsonResult = this.queryByCondition(pageParam);
        PageVO<UserVO> data = jsonResult.getData();
        List<ExportUserVO> exportUserVOList = new ArrayList<>(data.getContent().size());
        for (UserVO userVO : data.getContent()) {
            exportUserVOList.add(new ExportUserVO(userVO));
        }
        ExcelUtil.exportData(ExportUserVO.class, "????????????", exportUserVOList, response);
    }

    @Override
    public void generateGoogleQRCode(Integer userId, HttpServletResponse response) throws Exception {
        SysUser sysUser = this.getOne(new LambdaQueryWrapper<SysUser>().select(SysUser::getUsername, SysUser::getGoogleSecret).eq(SysUser::getId, userId));
        String url = GoogleAuthenticatorUtil.getQRBarcodeURL(sysUser.getUsername(), loginConfig.getCaptcha().getGoogleHost(), sysUser.getGoogleSecret());
        String filePath = generateQRCodeImg(GlobalProperties.getTmpSavePath(), url);
        ResponseUtil.downloadFileByLocal(response, new File(filePath), ContentTypeEnum.PNG);
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.simpleUUID());
    }
}
