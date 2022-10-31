package com.haoliang.common.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.haoliang.common.model.SysErrorLog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;


/**
 * @Description
 * @Author Dominick Li
 * @CreateTime 2022/10/24 19:25
 **/
@Data
public class ExportErrorLogVO {

    public ExportErrorLogVO(SysErrorLog sysErrorLog) {
        BeanUtils.copyProperties(sysErrorLog, this);
    }

    @ExcelProperty("异常发生的日期")
    private Date createDate;

    @ExcelProperty("节点IP")
    private String ipAddr;

    @ExcelProperty("错误类型")
    private String errorType;

    @ExcelProperty("错误内容")
    private String errorMsg;

    @ExcelProperty("异常发生的位置")
    private String position;

}
