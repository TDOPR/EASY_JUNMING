package com.haoliang.model.vo;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description 客户详细信息
 * @CreateTime 2022/12/9 9:32
 **/
@Data
public class MyDetailVO {
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
     * 平台说明
     */
    private String platformDesc;
    /**
     * 用户代理商等级
     */
    private Integer level;
}
