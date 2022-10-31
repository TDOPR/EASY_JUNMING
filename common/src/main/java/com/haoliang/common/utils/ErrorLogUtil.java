package com.haoliang.common.utils;


import com.haoliang.common.listen.InitListen;
import com.haoliang.common.model.SysErrorLog;
import com.haoliang.common.service.SysErrorLogService;

/**
 * @author Dominick Li
 * @description 把异常信息记录到错误日志表中
 **/
public class ErrorLogUtil {

    private ErrorLogUtil() {

    }

    public static void save(Exception e) {
        SysErrorLogService sysErrorLogService = SpringUtil.getBean(SysErrorLogService.class);
        String className = e.getClass().toString();
        className = className.substring(className.lastIndexOf(".") + 1);
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        SysErrorLog errorLog = new SysErrorLog(className, stackTraceElement.getFileName() + " lineNumber=" + stackTraceElement.getLineNumber(), e.toString(), InitListen.getServerIp());
        sysErrorLogService.save(errorLog);
    }



}
