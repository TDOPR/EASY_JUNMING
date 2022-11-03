package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleTypeEnum {
    USER(1, "user"),
    MANAGER(2, "manager"),
    ADMIN(3, "admin");

    private Integer code;
    private String name;

    public static RoleTypeEnum nameOf(String name) {
        for (RoleTypeEnum roleTypeEnum : values()) {
            if (roleTypeEnum.name.equals(name)) {
                return roleTypeEnum;
            }
        }
        return null;
    }


    public static RoleTypeEnum valueOf(Integer val) {
        for (RoleTypeEnum roleTypeEnum : values()) {
            if (roleTypeEnum.code.equals(val)) {
                return roleTypeEnum;
            }
        }
        return null;
    }

}
