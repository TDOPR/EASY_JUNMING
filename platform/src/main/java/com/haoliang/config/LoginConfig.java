package com.haoliang.config;

import com.haoliang.enums.CaptchaCategory;
import com.haoliang.enums.CaptchaType;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author Dominick Li
 * @CreateTime 2022/10/21 9:57
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "login")
public class LoginConfig {

    /**
     * 是否开启密码错误次数过多锁定
     */
    private boolean enableErrorLock;

    /**
     * 锁定的时间
     */
    private Integer lockTime;

    /**
     * 登录错误几次开始锁定
     */
    private Integer maxErrorNumber;

    @Autowired
    private Captcha captcha;

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "login.captcha")
    public class  Captcha{

        /**
         * 是否启用验证码
         */
        private  boolean enable;

        /**
         * 验证码类型
         */
        private CaptchaType type;

        /**
         * 验证码干扰类型
         */
        private CaptchaCategory category;

        /**
         * 数字验证码位数
         */
        private Integer numberLength;

        /**
         * 字符验证码长度
         */
        private Integer charLength;

        /**
         * 超时时间
         */
        private Integer expirationTime;
    }


}
