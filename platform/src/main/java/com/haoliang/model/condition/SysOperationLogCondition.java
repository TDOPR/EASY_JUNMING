package com.haoliang.model.condition;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haoliang.common.base.BaseCondition;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author Dominick Li
 * @Description 操作日志查询条件
 * @CreateTime 2022/11/4 16:10
 **/
@Data
public class SysOperationLogCondition extends BaseCondition {

    /**
     * 用户名 等值
     */
    private String username;

    /**
     * 模块 等值
     */
    private String module;
    /**
     * 操作 等值
     */
    private String description;

    @Override
    public QueryWrapper buildQueryParam() {
        this.buildBaseQueryWrapper();
        if (StringUtils.hasText(username)) {
            this.getQueryWrapper().eq("username", username);
        }
        if (StringUtils.hasText(module)) {
            this.getQueryWrapper().eq("module", module);
        }

        if (StringUtils.hasText(description)) {
            this.getQueryWrapper().eq("description", description);
        }
        return this.getQueryWrapper();
    }
}
