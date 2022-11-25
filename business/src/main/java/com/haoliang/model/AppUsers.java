package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCID;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 业务用户表  客户
 * @CreateTime 2022/11/1 10:30
 **/
@Data
@TableName("app_users")
public class AppUsers extends BaseModelCID {


    /**
     * 邮箱账号
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 加密用得盐
     *
     * @ignore
     */
    private String salt;

    /**
     * 头像
     */
    private String headImage;

    /**
     * 个性签名
     */
    private String autograph;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户状态 1=正常 0=禁用
     */
    private Integer enabled;

    /**
     * 登录次数
     */
    private Integer loginCount;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 邀请人Id
     */
    private Integer inviteId;

    /**
     * 代理商等级
     */
    private Integer level;

}
