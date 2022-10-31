package com.haoliang.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.haoliang.model.SysLoginLog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @Description 导出用户的模板
 * @Author Dominick Li
 * @CreateTime 2022/10/24 15:24
 **/
@Data
public class ExportLoginLogVO {

    public ExportLoginLogVO(SysLoginLog sysLoginLog) {
        BeanUtils.copyProperties(sysLoginLog, this);
    }

    @ExcelProperty("登录日期")
    private Date createTime;

    @ExcelProperty("登录的用户名")
    private String username;

    @ExcelProperty("登录的IP")
    private String ipAddr;

}
