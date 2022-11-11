package com.haoliang.model.condition;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haoliang.common.base.BaseCondition;
import com.haoliang.model.AppUserWithdraw;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 提现审核列表查询条件
 * @CreateTime 2022/11/11 12:06
 **/
@Data
public class AppUserWithdrawCondition extends BaseCondition<AppUserWithdraw> {

    /**
     * 用户邮箱号
     */
    private String email;

    /**
     * 审核状态 -1查所有 0=待审核 2=审核通过 3=驳回
     */
    private Integer auditStatus = -1;

    @Override
    public QueryWrapper buildQueryParam() {
        this.buildBaseQueryWrapper();
        if (auditStatus > -1) {
            this.getQueryWrapper().eq("auditStatus", auditStatus);
        } else {
            this.getQueryWrapper().gt("auditStatus", -1);
        }
        return this.getQueryWrapper();
    }
}
