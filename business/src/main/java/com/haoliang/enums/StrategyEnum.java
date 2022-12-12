package com.haoliang.enums;

import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.util.MessageUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominick Li
 * @Description 机器人做单策略枚举
 * @CreateTime 2022/12/9 18:40
 **/
@Getter
public enum StrategyEnum {

    ONE(1, "马丁格尔AI策略"),
    TWO(2,  "迈凯伦指数策略"),
    THREE(3,  "期现套利策略"),
    FOUR(4,  "波段追踪策略"),
    FIVE(5,  "频响定投策略"),
    six(6, "集中频响策略"),
    seven(7,  "逆周期跟单策略"),
    eight(8,  "低阻抗追踪策略"),
    nine(9,  "波段平衡策略"),
    ten(10,  "阻抗均衡策略"),
    ;

    /**
     * 类型Id
     */
    private Integer type;

    /**
     * 国际化的key
     */
    private String key;

    /**
     * 中文名称
     */
    private String name;

    /**
     * 国际化信息文件里的Key前缀
     */
    private final static String prefix = "strategy.";

    StrategyEnum(Integer type, String name) {
        this.type = type;
        this.key = prefix+ type;
        this.name = name;
    }

    /**
     * 获取所有类型Id
     */
    public static List<Integer> getTypeList() {
        List<Integer> idList = new ArrayList<>(values().length);
        for (StrategyEnum strategyEnum : values()) {
            idList.add(strategyEnum.getType());
        }
        return idList;
    }

    public static StrategyEnum typeOf(Integer type){
        for(StrategyEnum strategyEnum: values()){
            if(strategyEnum.getType().equals(type)){
                return strategyEnum;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return MessageUtil.get(key, ThreadLocalManager.getLanguage());
    }

}
