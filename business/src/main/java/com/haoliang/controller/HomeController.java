package com.haoliang.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haoliang.common.annotation.RepeatSubmit;
import com.haoliang.common.enums.BooleanEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.util.IpAddrUtil;
import com.haoliang.mapper.AppVersionsMapper;
import com.haoliang.model.AppVersions;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.model.dto.AppUserRegisterDTO;
import com.haoliang.model.dto.CheckVersionDTO;
import com.haoliang.model.dto.FindPasswordDTO;
import com.haoliang.model.vo.AppDownloadAddressVO;
import com.haoliang.model.vo.AppTokenVO;
import com.haoliang.model.vo.CheckVersionVO;
import com.haoliang.model.vo.HomeVO;
import com.haoliang.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 首页
 *
 * @author Dominick Li
 * @CreateTime 2022/11/8 17:34
 **/
@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private AppUserService appUserService;

    @Resource
    private AppVersionsMapper appVersionsMapper;

    /**
     * 主页信息
     */
    @GetMapping
    public JsonResult<HomeVO> home() {
        return appUserService.home();
    }

    /**
     * 注册
     */
    @RepeatSubmit
    @PostMapping("/register")
    public JsonResult register(@Valid @RequestBody AppUserRegisterDTO appUserRegisterDTO) {
        return appUserService.register(appUserRegisterDTO);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public JsonResult<AppTokenVO> login(@Valid @RequestBody AppUserLoginDTO appUserLogin, HttpServletRequest request) {
        return appUserService.login(appUserLogin, IpAddrUtil.getLocalIp(request));
    }

    /**
     * 找回密码
     */
    @PostMapping("/findPassword")
    public JsonResult findPassword(@Valid @RequestBody FindPasswordDTO findPasswordDTO) {
        return appUserService.findPassword(findPasswordDTO);
    }

    /**
     * 获取App下载链接
     */
    @GetMapping("/getAppDownloadUrl/{systemName}")
    public JsonResult getAppDownloadUrl(@PathVariable String systemName) {
        AppVersions appVersions = appVersionsMapper.selectOne(
                new LambdaQueryWrapper<AppVersions>()
                        .eq(AppVersions::getSystemName, systemName)
                        .eq(AppVersions::getActive, BooleanEnum.TRUE.getIntValue())
        );
        if (appVersions != null) {
            return JsonResult.successResult(new AppDownloadAddressVO(appVersions.getDownloadAddress()));
        }
        return JsonResult.failureResult();
    }

    /**
     * 检测版本更新信息
     */
    @PostMapping("/checkVersion")
    public JsonResult checkVersion(@RequestBody CheckVersionDTO checkVersionDTO) {
        AppVersions appVersions = appVersionsMapper.selectOne(
                new LambdaQueryWrapper<AppVersions>()
                        .eq(AppVersions::getSystemName, checkVersionDTO.getSystemName())
                        .eq(AppVersions::getActive, BooleanEnum.TRUE.getIntValue())
        );

        if (appVersions.getVersion().equals(checkVersionDTO.getVersion())) {
            return JsonResult.successResult(
                    CheckVersionVO.builder()
                            .flag(false)
                            .build()
            );
        }

        return JsonResult.successResult(
                CheckVersionVO.builder()
                        .flag(true)
                        .version(appVersions.getVersion())
                        .updateDesc(appVersions.getUpdateDesc())
                        .downloadAddress(appVersions.getDownloadAddress())
                        .force(BooleanEnum.TRUE.getIntValue().equals(appVersions.getForceUpdate()))
                        .build()
        );
    }

}
