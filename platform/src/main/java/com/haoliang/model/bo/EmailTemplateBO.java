package com.haoliang.model.bo;

import lombok.Data;

/**
 * @author Dominick Li
 * @description 重xml中获取发送邮件的模版
 **/
@Data
public class EmailTemplateBO {

    /**
     * 邮件标题
     */
    private String title;

    /**
     * 邮件内容
     */
    private String content;

}
