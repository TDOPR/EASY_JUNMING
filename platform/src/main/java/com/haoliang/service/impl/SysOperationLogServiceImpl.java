package com.haoliang.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.utils.IpAddrUtil;
import com.haoliang.common.utils.PointUtils;
import com.haoliang.config.JwtTokenConfig;
import com.haoliang.mapper.SysOperationLogMapper;
import com.haoliang.model.SysOperationLog;
import com.haoliang.service.SysOperationLogService;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog> implements SysOperationLogService {

    @Autowired
    private JwtTokenConfig jwtTokenConfig;

    @Override
    public void saveOperationLog(JoinPoint joinPoint, String methodName, String module, String description) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        SysOperationLog sysOperationLog = new SysOperationLog();
        sysOperationLog.setIpAddr(IpAddrUtil.getIpAddr(request));
        sysOperationLog.setModule(module);
        sysOperationLog.setDescription(description);
        sysOperationLog.setUsername(jwtTokenConfig.getUserNameFromToken(request.getHeader("token")));
        sysOperationLog.setContent(JSONObject.toJSONString(PointUtils.getRequestParamsByJoinPoint(joinPoint)));
        this.save(sysOperationLog);
    }


}
