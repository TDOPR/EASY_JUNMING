package com.haoliang.common.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoliang.common.utils.DateUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Dominick Li
 * @Description 封装页面的查询条件
 * @CreateTime 2022/11/4 15:55
 **/
@Data
public abstract class BaseCondition<T> {

    /**
     * 范围 开始时间 格式: yyyy-MM-dd
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date beginDate;

    /**
     * 范围 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date endDate;

    /**
     * 查询条件构造器
     * @ignore
     */
    private QueryWrapper<T> queryWrapper;

    /**
     * 构建通用查询条件
     */
    protected void buildBaseQueryWrapper() {
        if (this.queryWrapper == null) {
            this.queryWrapper = new QueryWrapper<>();
            //排序条件
            this.queryWrapper.orderByDesc("createTime");
            if (!StringUtils.isEmpty(this.getBeginDate())) {
                //大于或等于传入时间
                this.queryWrapper.ge("createTime", this.getBeginDate());
            }
            if (!StringUtils.isEmpty(this.getEndDate())) {
                endDate = DateUtil.getDateStrIncrement(endDate, 1, TimeUnit.DAYS);
                //小于或等于传入时间
                this.queryWrapper.le("createTime", endDate);
            }
        }
    }

    /**
     * 构建查询条件
     */
    public abstract QueryWrapper buildQueryParam();


}
