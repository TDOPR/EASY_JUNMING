package com.haoliang.model.condition;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haoliang.common.base.BaseCondition;
import com.haoliang.model.AppUsers;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 客户查询条件
 * @CreateTime 2022/11/11 18:52
 **/
@Data
public class AppUsersCondition extends BaseCondition<AppUsers> {

    /**
     * 邮箱账号
     */
    private String email;

    /**
     * 用户状态 -1查所有 1=正常 0=禁用
     */
    private Integer enabled;


    @Override
    public QueryWrapper buildQueryParam() {
        return null;
    }
}
