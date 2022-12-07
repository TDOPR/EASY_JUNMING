package com.haoliang.model.vo;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/10 12:08
 **/
@Data
public class AppTokenVO {
    /**
     * token信息
     */
    private String token;
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
     * 用户邀请码
     */
    private String inviteCode;
    /**
     * 系统版本
     */
    private String version;
    /**
     * 平台说明
     */
    private String platformDesc;
}
