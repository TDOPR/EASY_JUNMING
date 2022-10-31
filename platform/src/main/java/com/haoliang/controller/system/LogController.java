package com.haoliang.controller.system;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.SysErrorLog;
import com.haoliang.common.model.vo.ExportErrorLogVO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.utils.excel.ExcelUtil;
import com.haoliang.model.SysLoginLog;
import com.haoliang.model.SysOperationLog;
import com.haoliang.model.vo.ExportLoginLogVO;
import com.haoliang.model.vo.ExportOperationLogVO;
import com.haoliang.common.service.SysErrorLogService;
import com.haoliang.service.SysLoginLogService;
import com.haoliang.service.SysOperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominick Li
 * @description 日志管理
 **/
@RestController
@RequestMapping("/admin")
public class LogController {

    @Autowired
    private SysErrorLogService sysErrorLogService;

    @Autowired
    private SysOperationLogService sysOperationLogService;

    @Autowired
    private SysLoginLogService sysLoginLogService;

    @PostMapping("/loginlog/queryByCondition")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult loginlogList(@RequestBody PageParam<SysLoginLog> pageParam) {
        IPage<SysLoginLog> iPage = sysLoginLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    @OperationLog(module = "登录日志", description = "批量删除")
    @PostMapping("/loginlog/deleteByIds")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult deleteLoginLogsByIds(@RequestBody String idList) {
        return JsonResult.build(sysLoginLogService.removeByIds(JSONArray.parseArray(idList, Integer.class)));
    }

    @PostMapping("/loginlog/export")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void exportLoginLog(@RequestBody PageParam<SysLoginLog> pageParam, HttpServletResponse response) {
        IPage<SysLoginLog> iPage = sysLoginLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        List<SysLoginLog> sysLoginLogList = iPage.getRecords();
        List<ExportLoginLogVO> exportLoginLogVOList = new ArrayList<>(sysLoginLogList.size());
        for (SysLoginLog sysLoginLog : sysLoginLogList) {
            exportLoginLogVOList.add(new ExportLoginLogVO(sysLoginLog));
        }
        ExcelUtil.exportData(ExportLoginLogVO.class, "登录日志", exportLoginLogVOList, response);
    }

    @PostMapping("/operationlog/queryByCondition")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult operationlogList(@RequestBody PageParam<SysOperationLog> pageParam) {
        IPage<SysOperationLog> iPage = sysOperationLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    @PostMapping("/operationlog/export")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void exportOperationlog(@RequestBody PageParam<SysOperationLog> pageParam, HttpServletResponse response) {
        IPage<SysOperationLog> iPage = sysOperationLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        List<SysOperationLog> sysOperationLogList = iPage.getRecords();
        List<ExportOperationLogVO> exportOperationLogVOList = new ArrayList<>(sysOperationLogList.size());
        for (SysOperationLog sysOperationLog : sysOperationLogList) {
            exportOperationLogVOList.add(new ExportOperationLogVO(sysOperationLog));
        }
        ExcelUtil.exportData(ExportOperationLogVO.class, "操作日志", exportOperationLogVOList, response);
    }

    @PostMapping("/errorlog/queryByCondition")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult errorlogList(@RequestBody PageParam<SysErrorLog> pageParam) {
        IPage<SysErrorLog> iPage = sysErrorLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    @OperationLog(module = "错误日志", description = "批量删除")
    @PostMapping("/errorlog/deleteByIds")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult deleteErrorLogsByIds(@RequestBody String idList) {
        return JsonResult.build(sysErrorLogService.removeByIds(JSONObject.parseArray(idList, Integer.class)));
    }

    @PostMapping("/errorlog/export")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void exportErrorlog(@RequestBody PageParam<SysErrorLog> pageParam, HttpServletResponse response) {
        IPage<SysErrorLog> iPage = sysErrorLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        List<SysErrorLog> sysErrorLogList = iPage.getRecords();
        List<ExportErrorLogVO> errorLogVOList = new ArrayList<>(sysErrorLogList.size());
        for (SysErrorLog sysErrorLog : sysErrorLogList) {
            errorLogVOList.add(new ExportErrorLogVO(sysErrorLog));
        }
        ExcelUtil.exportData(ExportErrorLogVO.class, "错误日志", errorLogVOList, response);
    }


}
