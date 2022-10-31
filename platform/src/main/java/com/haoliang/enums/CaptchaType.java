package com.haoliang.enums;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import com.haoliang.common.generate.UnsignedMathGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码类型
 *
 * @author Lion Li
 */
@Getter
@AllArgsConstructor
public enum CaptchaType {

    /**
     * 数字
     */
    MATH(UnsignedMathGenerator.class),

    /**
     * 字符
     */
    CHAR(RandomGenerator.class),

    /**
     * 随机
     */
    RANDOM(null);


    private final Class<? extends CodeGenerator> clazz;
}
