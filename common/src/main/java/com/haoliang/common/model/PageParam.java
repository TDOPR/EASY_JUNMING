package com.haoliang.common.model;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haoliang.common.base.BaseCondition;
import lombok.Data;


/**
 * @author Dominick Li
 * @CreateTime 2020/3/20 15:45
 * @description
 **/
@Data
public class PageParam<T,C extends BaseCondition> {

    /**
     * 分页对象
     * @ignore
     */
    private Page<T> page;

    /**
     * 当前页 默认值为1
     */
    private Integer currentPage;

    /**
     * 每页显示的数量 默认值为10
     */
    private Integer pageSize = 10;

    /**
     * 查询条件
     */
    private C searchParam;

    public Page<T> getPage() {
        if(this.page==null){
            if (this.currentPage == null) {
                this.currentPage = 1;
            }
            if (this.pageSize == null) {
                this.pageSize = 10;
            }
            this.page = new Page<>(this.currentPage, this.pageSize);
        }
        return page;
    }


}
