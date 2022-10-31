package com.haoliang.common.model.vo;

import lombok.Data;

/**
 * @Description
 * @Author Dominick Li
 **/
@Data
public class CaptchaVO {

    public CaptchaVO(String uuid, String captchaImagel) {
        this.uuid = uuid;
        this.captchaImagel = captchaImagel;
    }

    private String uuid;

    private String captchaImagel;

}
