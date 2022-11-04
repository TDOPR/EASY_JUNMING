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
public class SysUserCondition extends BaseCondition{

    /**
     * 用户名 模糊
     */
    private String username;

    /**
     * 启用状态 1=正常,0=禁用，''=查全部
     */
    private String enabled;

    /**
     * 删除标识 1=已删除,0=未删除, ''=查全部
     */
    private String deleted;

    @Override
    public QueryWrapper buildQueryParam() {
        this.buildBaseQueryWrapper();
        if (StringUtils.hasText(username)) {
            username = username.replaceAll("%", "////%").replaceAll("_", "////_");
            this.getQueryWrapper().like("username", username);
        }
        if (StringUtils.hasText(enabled)) {
            this.getQueryWrapper().eq("enabled", enabled);
        }

        if (StringUtils.hasText(deleted)) {
            this.getQueryWrapper().eq("deleted", deleted);
        }
        return this.getQueryWrapper();
    }
}
