package com.haoliang.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Dominick Li
 * @Description 机器人等级枚举
 * @CreateTime 2022/11/1 10:30
 **/
@Getter
@AllArgsConstructor
public enum RobotLevelEnum {

    ONE(1,100,1000),
    TWO(2,200,5000),
    THREE(3,300,10000),
    FOUR(4,400,30000),
    FIVE(5,500,null);

    /**
     * 代理商等级
     */
    private Integer level;

    /**
     * 机器人金额
     */
    private Integer money;

    /**
     * 托管金额上限 机器人等级5的时候则没有
     */
    private Integer trusteeshipMoney;



    public static RobotLevelEnum getByLevel(Integer level) {
        for (RobotLevelEnum robotLevelEnum : values()) {
            if (robotLevelEnum.getLevel().equals(level)) {
                return robotLevelEnum;
            }
        }
        return null;
    }

}
