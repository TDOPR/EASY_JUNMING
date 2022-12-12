package com.haoliang.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@Getter
@AllArgsConstructor
public enum LanguageEnum {

    EN_US("en_US", "英语/美国"),
    ES_ES("es_ES", "西班牙语/西班牙"),
    PT_PT("pt_PT", "葡萄牙语/葡萄牙"),
    ZH_CH("zh_CN", "中文/中国");

    /**
     * 语言_国家缩写
     */
    private String name;

    /**
     * 描述
     */
    private String desc;

    /**
     * 根据语言名称获取对应枚举
     * @param name
     * @return 默认返回中文
     */
    private static LanguageEnum nameOf(String name) {
        for (LanguageEnum languageEnum : values()) {
            if (languageEnum.getName().equals(name)) {
                return languageEnum;
            }
        }
        return ZH_CH;
    }

    public static  Locale getLocale(String name){
        System.out.println("国际化语言="+name+","+nameOf(name).getName());
        return new Locale(nameOf(name).getName());
    }
}
