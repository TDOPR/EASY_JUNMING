package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCIDNoModifyTime;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 验证码
 * @CreateTime 2022/11/1 10:50
 **/
@TableName("verification_codes")
@Data
public class VerificationCodes extends BaseModelCIDNoModifyTime {

    /**
     * 发送的用户Id
     */
    private Integer userId;

    /**
     * 验证码
     */
    private String code;

    /**
     * 发送场景
     */
    private String scene;

    /**
     * 发送场景 1=sms手机短信  0=邮箱
     */
    private Integer type;

    /**
     * 发送ip
     */
    private String ip;

}
