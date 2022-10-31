package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModel;
import lombok.Data;

/**
 * @author Dominick Li
 * @description 系统字典表
 **/
@Data
@TableName("sys_dictionary")
public class SysDictionary extends BaseModel {

    /**
     * 字典的key
     */
    private String dicKey;
    /**
     * 字典的value
     */
    private String dicValue;
    /**
     * 字典类型名称,区分一组类型
     */
    private String dicName;

}
