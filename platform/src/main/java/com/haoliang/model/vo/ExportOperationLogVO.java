package com.haoliang.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.haoliang.common.model.SysOperationLog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @Description
 * @Author Dominick Li
 * @CreateTime 2022/10/24 19:32
 **/
@Data
public class ExportOperationLogVO {

    public ExportOperationLogVO(SysOperationLog sysOperationLog) {
        BeanUtils.copyProperties(sysOperationLog, this);
    }

    @ExcelProperty("操作日期")
    private Date createTime;

    @ExcelProperty("操作员")
    private String username;

    @ExcelProperty("操作的IP")
    private String ipAddr;

    @ExcelProperty("模块")
    private String module;

    @ExcelProperty("操作类型")
    private String description;

    @ExcelProperty("操作数据")
    private String content;

}
