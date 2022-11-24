package com.haoliang.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum  BooleanEnum {

    TRUE(1),
    FALSE(0);

    private Integer intValue;
}
