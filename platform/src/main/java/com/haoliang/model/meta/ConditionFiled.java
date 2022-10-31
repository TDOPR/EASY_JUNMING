package com.haoliang.model.meta;

import com.haoliang.enums.ConditionTypeEnum;
import lombok.Data;


/**
 * @author Dominick Li
 * @description 查询字段信息
 **/
@Data
public class ConditionFiled implements Comparable<ConditionFiled> {

    /**
     * 字段名称
     */
    private String name;

    /**
     * 字段编码
     */
    private String code;

    /**
     * 查询条件类型
     */
    private ConditionTypeEnum type;

    /**
     * 查询条件的值
     */
    private String value;

    /**
     * 日期范围查询条件
     */
    private java.util.List<String> betweenValue;

    public ConditionFiled(){}

    public ConditionFiled(String name, String value, ConditionTypeEnum conditionTypeEnum) {
        this.name = name;
        this.value = value;
        this.type=conditionTypeEnum;
    }

    public ConditionFiled(String name, String code, String dataType) {
        this.name = name;
        this.code = code;
        this.type=ConditionTypeEnum.getValueByType(dataType);
    }

    @Override
    public int compareTo(ConditionFiled o) {
        return this.getType().getValue() - o.getType().getValue();
    }
}
