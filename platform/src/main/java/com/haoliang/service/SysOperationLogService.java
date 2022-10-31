package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.SysOperationLog;
import org.aspectj.lang.JoinPoint;

public interface SysOperationLogService extends IService<SysOperationLog> {

    void saveOperationLog(JoinPoint joinPoint, String methodName, String module, String description);

}
