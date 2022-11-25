package com.haoliang.model.dto;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/24 17:27
 **/
@Data
public class PageDTO {

    /**
     * 当前页
     */
    private Integer currentPage=1;

    /**
     * 每页显示的数据
     */
    private Integer pageSize=10;

    /**
     * 是否第一次加载
     */
    private boolean init = true;
}
