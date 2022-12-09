package com.haoliang.common.util;


import com.haoliang.common.enums.LanguageEnum;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * @author Dominick Li
 * @Description 国际化转换工具类
 * @CreateTime 2022/12/8 16:28
 **/
public class MessageUtil {

    /**
     * 根据key信息获取对应语言的内容
     * @param key
     * @param language
     * @return
     */
    public static String get(String key, String language) {
        return get(key, LanguageEnum.getLocale(language));
    }

    public static String get(String key, Locale language) {
        return getInstance().getMessage(key, new String[0], language);
    }

    public static String get(String key, Object[] params, String language) {
        return get(key, params, LanguageEnum.getLocale(language));
    }

    public static String get(String key, Object[] params, Locale language) {
        return getInstance().getMessage(key, params, language);
    }

    private static MessageSource getInstance() {
        return Lazy.messageSource;
    }

    /**
     * 使用懒加载方式实例化messageSource国际化工具
     */
    private static class Lazy {
        private static final MessageSource messageSource = SpringUtil.getBean(MessageSource.class);
    }
}
