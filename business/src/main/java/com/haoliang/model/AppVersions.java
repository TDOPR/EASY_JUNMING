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
     * 如需要换行 请再记事本中编辑好再复制到数据库中
     */
    private String updateDesc;

    /**
     * 英语 功能更新说明
     */
    private String enUpdateDesc;

    /**
     * 葡萄牙语 功能更新说明
     */
    private String ptUpdateDesc;

    /**
     * 西班牙语 功能更新说明
     */
    private String esUpdateDesc;

    /**
     * app下载地址
     */
    private String downloadAddress;

    /**
     * 激活版本  1=当前最新版本  0=旧版本
     */
    private Integer active;

    /**
     * 是否强制升级  1=强制更新  0=需要确认才能更新
     */
    private Integer forceUpdate;
}
