package com.haoliang.common.model;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * @author Dominick Li
 * @CreateTime 2020/3/20 15:45
 * @description
 **/
@Data
public class PageParam<T> {

    /**
     * 分页对象
     * @ignore
     */
    private Page<T> page;

    /**
     * 查询条件构造器
     * @ignore
     */
    private QueryWrapper<T> queryWrapper;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页显示的数量
     */
    private Integer pageSize = 10;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date beginDate;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date endDate;

    /**
     * 等值查询条件存储
     */
    private Map<String, Object> eq = new HashMap<>();

    /**
     * 模糊查询条件存储
     */
    private Map<String, String> like = new HashMap<>();

    public Page<T> getPage() {
        if(this.page==null){
            if (this.currentPage == null) {
                this.currentPage = 1;
            }
            if (this.pageSize == null) {
                this.pageSize = 10;
            }
            this.page = new Page<>(this.currentPage, this.pageSize);
            this.setCompare();
        }
        return page;
    }

    /**
     * 注入查询条件参数
     */
    private void setCompare() {
        this.queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("createTime");
        if (!StringUtils.isEmpty(beginDate)) {
            //大于或等于传入时间
            queryWrapper.ge("createTime", beginDate);
        }
        if (!StringUtils.isEmpty(endDate)) {
            //小于或等于传入时间
            queryWrapper.le("createTime", endDate);
        }

        String value = "";
        //注入文本框的模糊查询参数
        for (Map.Entry<String, String> entry : like.entrySet()) {
            value = entry.getValue().trim();
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            //过滤掉非法的符号,不然会影响模糊查询
            value = value.replaceAll("%", "////%").replaceAll("_", "////_");
            queryWrapper.like(entry.getKey(), value);
        }

        //注入下拉框或单选框的等值查询参数
        for (Map.Entry<String, Object> entry : eq.entrySet()) {
            if (StringUtils.isEmpty(entry.getValue())) {
                continue;
            }
            queryWrapper.eq(entry.getKey(), entry.getValue());
        }
    }
}
