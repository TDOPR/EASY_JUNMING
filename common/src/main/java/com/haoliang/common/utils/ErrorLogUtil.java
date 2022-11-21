package com.haoliang.common.utils;


import com.haoliang.common.lister.InitListen;
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

    /**
     * 保存错误信息
     * @param classz 类名称
     * @param position 位置
     * @param message 错误信息
     */
    public static void save(Class classz, String position, String message) {
        SysErrorLogService sysErrorLogService = SpringUtil.getBean(SysErrorLogService.class);
        SysErrorLog errorLog = new SysErrorLog(classz.getName(), position, message, InitListen.getServerIp());
        sysErrorLogService.save(errorLog);
    }

}
