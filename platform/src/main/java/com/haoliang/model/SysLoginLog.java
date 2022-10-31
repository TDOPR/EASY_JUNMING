package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCIDNoModifyTime;
import lombok.Data;

/**
 * @author Dominick Li
 * @description 登录日志表
 **/
@Data
@TableName("sys_loginlog" )
public class SysLoginLog extends BaseModelCIDNoModifyTime {


    /**
     * 用户名
     */
    private String username;

    /**
     * 登录Ip地址
     */
    private String ipAddr;

    public SysLoginLog(){}

    public SysLoginLog(String username, String ipAddr){
        this.ipAddr=ipAddr;
        this.username=username;
    }
}
