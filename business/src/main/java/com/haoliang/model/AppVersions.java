package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCIDNoModifyTime;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 系统版本表
 * @CreateTime 2022/11/22 9:36
 **/
@Data
@TableName("app_versions")
public class AppVersions  extends BaseModelCIDNoModifyTime {

    /**
     * 系统名称 ios 、android
     */
    private String systemName;

    /**
     * 版本号
     */
    private String version;

    /**
     * 功能更新说明
     */
    private String updateDesc;

    /**
     * 平台描述
     */
    private String platformDesc;

    /**
     * app下载地址
     */
    private String downloadAddress;

}
