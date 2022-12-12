package com.haoliang.server;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haoliang.common.annotation.RedisLock;
import com.haoliang.common.config.AppParamProperties;
import com.haoliang.common.config.GlobalProperties;
import com.haoliang.common.config.SysSettingParam;
import com.haoliang.common.model.SysErrorLog;
import com.haoliang.common.model.SysLoginLog;
import com.haoliang.common.model.SysOperationLog;
import com.haoliang.common.model.dto.WorkspaceHarDiskInfo;
import com.haoliang.common.service.SysErrorLogService;
import com.haoliang.common.service.SysLoginLogService;
import com.haoliang.common.service.SysOperationLogService;
import com.haoliang.common.util.DateUtil;
import com.haoliang.common.util.GetWorkspaceHarDiskInfoUtil;
import com.haoliang.model.dto.EmailTemplateDTO;
import com.haoliang.netty.AdminHomeWebSocketHandler;
import com.haoliang.service.SystemService;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
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
    private AppParamProperties appParamProperties;

    @Autowired
    private SysErrorLogService sysErrorLogService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @Autowired
    private SystemService systemService;

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
        LocalDateTime localDate = LocalDateTime.now();
        if (SysSettingParam.getErrorLogSaveDay() >= 0) {
            sysErrorLogService.remove(new LambdaQueryWrapper<SysErrorLog>().le(SysErrorLog::getCreateTime, DateUtil.getDateStrIncrement(localDate, -SysSettingParam.getErrorLogSaveDay(), TimeUnit.DAYS)));
        }
        if (SysSettingParam.getLoginLogSaveDay() >= 0) {
            sysLoginLogService.remove(new LambdaQueryWrapper<SysLoginLog>().le(SysLoginLog::getCreateTime, DateUtil.getDateStrIncrement(localDate, -SysSettingParam.getLoginLogSaveDay(), TimeUnit.DAYS)));
        }
        if (SysSettingParam.getOperationLogSaveDay() >= 0) {
            sysOperationLogService.remove(new LambdaQueryWrapper<SysOperationLog>().le(SysOperationLog::getCreateTime, DateUtil.getDateStrIncrement(localDate, -SysSettingParam.getOperationLogSaveDay(), TimeUnit.DAYS)));
        }
        //清理临时文件
        FileUtil.del(new File(GlobalProperties.getTmpSavePath()));
    }

    /**
     * 每小时监控硬盘使用率情况
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void monitorDiskUse() {
        if (!enableMail) {
            return;
        }
        String savePath = appParamProperties.getRootPath();
        //设置硬盘使用率超过多少发送邮件通知
        WorkspaceHarDiskInfo workspaceHarDiskInfo = GetWorkspaceHarDiskInfoUtil.getWorkspaceHarDiskInfo(savePath);
        //WorkspaceHarDiskInfo workspaceHarDiskInfo = new WorkspaceHarDiskInfo("dev/mapper/centos-root", "453G", "176G", "81%");
        if (workspaceHarDiskInfo == null) {
            log.error(String.format("未获取 {} 相关的磁盘信息", savePath));
            return;
        }
        log.info(String.format("目录所属文件系统: %s  ,硬盘使用详情: %s/%s ,使用率达到：%s", workspaceHarDiskInfo.getFilesystem(), workspaceHarDiskInfo.getUsed(), workspaceHarDiskInfo.getSize(), workspaceHarDiskInfo.getUse()));
        if (Integer.parseInt(workspaceHarDiskInfo.getUse().substring(0, workspaceHarDiskInfo.getUse().length() - 1)) > SysSettingParam.getThresholdSize()) {
            log.error("已超过阔值" + SysSettingParam.getThresholdSize() + "%,请及时处理，避免服务因存储不足导致异常的情况");
            String content = monitorInfoTemplate.getContent();
            content = content.replace("{{serverName}}", appParamProperties.getServerName());
            content = content.replace("{{info}}", String.format("所属文件系统: %s  ,硬盘使用: %s/%s ,使用率达到：%s", workspaceHarDiskInfo.getFilesystem(), workspaceHarDiskInfo.getUsed(), workspaceHarDiskInfo.getSize(), workspaceHarDiskInfo.getUse()));
            content = content.replace("{{rate}}", SysSettingParam.getThresholdSize() + "%");
            monitorInfoTemplate.setContent(content);
            EmailServer.send(monitorInfoTemplate);
        } else {
            log.info("硬盘已使用:{},未达到阔值:{}", workspaceHarDiskInfo.getUse(), SysSettingParam.getThresholdSize());
        }
    }

    /**
     * 每30秒推送一次系统后台主页消息
     */
    @Scheduled(fixedDelay =30000 )
    public void sendSystemInfo() {
        if(AdminHomeWebSocketHandler.getChannelGroup().size()>0){
            log.info("推送后台主页信息到客户端");
            AdminHomeWebSocketHandler.getChannelGroup().writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(systemService.getHomeInfo().getData())));
        }
    }

}
