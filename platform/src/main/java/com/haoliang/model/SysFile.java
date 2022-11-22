package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.haoliang.common.base.BaseModelCID;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description系统文件
 * @CreateTime 2022/11/22 16:40
 **/
@Data
@TableName("sys_file")
public class SysFile extends BaseModelCID {

    /**
     * 上传着
     */
    private String username;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件描述
     */
    private String fileDesc;

    /**
     * 文件存储地址
     */
    @JsonIgnore
    private String filePath;

    /**
     * 文件下载地址
     */
    private String downloadPath;

}
