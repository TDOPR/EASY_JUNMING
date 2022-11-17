package com.haoliang.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/11 18:51
 **/
@Data
public class AppUsersVO {

    /**
     * 用户Id
     */
    private Integer id;

    /**
     * 邮箱账号
     */
    private String email;

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
     * 用户等级
     */
    private Integer level;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 钱包余额
     */
    private BigDecimal walletAmount;

    /**
     * 托管本金
     */
    private BigDecimal principalAmount;

    /**
     * 机器人等级
     */
    private Integer robotLevel;

    /**
     * 下级数量
     */
    private Integer subordinateNumber;

}
