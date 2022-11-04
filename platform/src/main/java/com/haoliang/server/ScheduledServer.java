package com.haoliang.server;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.common.model.SysErrorLog;
import com.haoliang.common.model.WorkspaceHarDiskInfo;
import com.haoliang.common.service.SysErrorLogService;
import com.haoliang.common.utils.DateUtil;
import com.haoliang.common.utils.GetWorkspaceHarDiskInfoUtil;
import com.haoliang.config.AppParam;
import com.haoliang.config.DictionaryParam;
import com.haoliang.model.dto.EmailTemplateDTO;
import com.haoliang.common.model.SysLoginLog;
import com.haoliang.common.model.SysOperationLog;
import com.haoliang.common.service.SysLoginLogService;
import com.haoliang.common.service.SysOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @author Dominick Li
 * @CreateTime 2020/3/19 15:05
 * @description
 **/
@Component
@Slf4j
public class ScheduledServer {

    @Resource(name = "monitorDisk")
    private EmailTemplateDTO monitorInfoTemplate;

    @Autowired
    private AppParam appParam;

    @Autowired
    private SysErrorLogService sysErrorLogService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Value("${mail.enable}")
    private boolean enableMail;

    /**
     * 每天晚上1点
     * 清理过期的数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @RedisLock
    public void deletExpiredFiles() {
        log.info("-------------执行清理过期文件任务--------------");
        //清理登录日志,操作日志,错误日志
        Date nowDate = new Date();
        if (DictionaryParam.getErrorLogSaveDay() >= 0) {
            sysErrorLogService.remove(new LambdaQueryWrapper<SysErrorLog>().le(SysErrorLog::getCreateTime, DateUtil.getDateStrIncrement(nowDate, -DictionaryParam.getErrorLogSaveDay(), TimeUnit.DAYS)));
        }
        if (DictionaryParam.getLoginLogSaveDay() >= 0) {
            sysLoginLogService.remove(new LambdaQueryWrapper<SysLoginLog>().le(SysLoginLog::getCreateTime, DateUtil.getDateStrIncrement(nowDate, -DictionaryParam.getLoginLogSaveDay(), TimeUnit.DAYS)));
        }
        if (DictionaryParam.getOperationLogSaveDay() >= 0) {
            sysOperationLogService.remove(new LambdaQueryWrapper<SysOperationLog>().le(SysOperationLog::getCreateTime, DateUtil.getDateStrIncrement(nowDate, -DictionaryParam.getOperationLogSaveDay(), TimeUnit.DAYS)));
        }
    }

    /**
     * 每小时监控硬盘使用率情况
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void monitorDiskUse() {
        if (!enableMail) {
            return;
        }
        String savePath = appParam.getRootPath();
        //设置硬盘使用率超过多少发送邮件通知
        WorkspaceHarDiskInfo workspaceHarDiskInfo = GetWorkspaceHarDiskInfoUtil.getWorkspaceHarDiskInfo(savePath);
        //WorkspaceHarDiskInfo workspaceHarDiskInfo = new WorkspaceHarDiskInfo("dev/mapper/centos-root", "453G", "176G", "81%");
        if (workspaceHarDiskInfo == null) {
            log.error(String.format("未获取 {} 相关的磁盘信息", savePath));
            return;
        }
        log.info(String.format("目录所属文件系统: %s  ,硬盘使用详情: %s/%s ,使用率达到：%s", workspaceHarDiskInfo.getFilesystem(), workspaceHarDiskInfo.getUsed(), workspaceHarDiskInfo.getSize(), workspaceHarDiskInfo.getUse()));
        if (Integer.parseInt(workspaceHarDiskInfo.getUse().substring(0, workspaceHarDiskInfo.getUse().length() - 1)) > DictionaryParam.getThresholdSize()) {
            log.error("已超过阔值" + DictionaryParam.getThresholdSize() + "%,请及时处理，避免服务因存储不足导致异常的情况");
            String content = monitorInfoTemplate.getContent();
            content = content.replace("{{serverName}}", appParam.getServerName());
            content = content.replace("{{info}}", String.format("所属文件系统: %s  ,硬盘使用: %s/%s ,使用率达到：%s", workspaceHarDiskInfo.getFilesystem(), workspaceHarDiskInfo.getUsed(), workspaceHarDiskInfo.getSize(), workspaceHarDiskInfo.getUse()));
            content = content.replace("{{rate}}", DictionaryParam.getThresholdSize() + "%");
            monitorInfoTemplate.setContent(content);
            EmailServer.send(monitorInfoTemplate);
        } else {
            log.info("硬盘已使用:{},未达到阔值:{}", workspaceHarDiskInfo.getUse(), DictionaryParam.getThresholdSize());
        }
    }

}
