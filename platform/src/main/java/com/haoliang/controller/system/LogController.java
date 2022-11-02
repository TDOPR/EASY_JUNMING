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
 * 日志管理
 * @author Dominick Li
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

    /**
     * 分页查询登录日志
     */
    @PostMapping("/loginlog/queryByCondition")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult<PageVO<SysLoginLog>> loginlogList(@RequestBody PageParam<SysLoginLog> pageParam) {
        IPage<SysLoginLog> iPage = sysLoginLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    /**
     * 批量删除登录日志
     * @param idList id数组  格式[1,2,3]
     */
    @OperationLog(module = "登录日志", description = "批量删除")
    @PostMapping("/loginlog/deleteByIds")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult deleteLoginLogsByIds(@RequestBody String idList) {
        return JsonResult.build(sysLoginLogService.removeByIds(JSONArray.parseArray(idList, Integer.class)));
    }

    /**
     * 导出登录日志
     * @return 文件二进制流
     */
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

    /**
     * 分页查询操作日志
     */
    @PostMapping("/operationlog/queryByCondition")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult<PageVO<SysOperationLog>> operationlogList(@RequestBody PageParam<SysOperationLog> pageParam) {
        IPage<SysOperationLog> iPage = sysOperationLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    /**
     * 导出操作日志
     * @return 文件二进制流
     */
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

    /**
     * 分页查询错误日志
     */
    @PostMapping("/errorlog/queryByCondition")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult<PageVO<SysErrorLog>> errorlogList(@RequestBody PageParam<SysErrorLog> pageParam) {
        IPage<SysErrorLog> iPage = sysErrorLogService.page(pageParam.getPage(), pageParam.getQueryWrapper());
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    /**
     * 批量删除错误日志
     * @param idList id数组
     */
    @OperationLog(module = "错误日志", description = "批量删除")
    @PostMapping("/errorlog/deleteByIds")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public JsonResult deleteErrorLogsByIds(@RequestBody String idList) {
        return JsonResult.build(sysErrorLogService.removeByIds(JSONObject.parseArray(idList, Integer.class)));
    }

    /**
     * 批量导出错误日志
     * @return 文件二进制流
     */
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
