package com.haoliang.model.condition;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haoliang.common.base.BaseCondition;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/4 16:10
 **/
@Data
public class SysRoleCondition extends BaseCondition{

    /**
     * 角色名称 等值
     */
    private String roleName;

    /**
     * 角色编码 等值
     */
    private String roleCode;

    @Override
    public QueryWrapper buildQueryParam() {
        this.buildBaseQueryWrapper();
        if (StringUtils.hasText(roleName)) {
            this.getQueryWrapper().eq("roleName", roleName);
        }
        if (StringUtils.hasText(roleCode)) {
            this.getQueryWrapper().eq("roleCode", roleCode);
        }
        return this.getQueryWrapper();
    }
}
