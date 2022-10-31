package com.haoliang.model.meta;

import lombok.Data;

import java.util.List;


/**
 * @author Dominick Li
 * @description 通用查询条件
 **/
@Data
public class UniversalQueryParamBO {

    /**
     * 初始化加载
     */
    private boolean firstInit;

    /**
     * 表的id
     */
    private Long metaId;

    /**
     * 当前页
     * @required
     */
    private Integer currentPage;

    /**
     * 每页显示的数量
     * @required
     */
    private Integer pageSize;

    /**
     * 查询条件列表
     */
    private List<ConditionFiled> conditionList;



}
