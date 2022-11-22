package com.haoliang.model.condition;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haoliang.common.base.BaseCondition;
import com.haoliang.model.SysNotice;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/7 11:24
 **/
@Data
public class SysNoticeCondition extends BaseCondition<SysNotice> {

    /**
     * 中文标题
     */
    private String cnTitle;

    /**
     * 英文标题
     */
    private String enTitle;

    @Override
    public QueryWrapper buildQueryParam() {
        this.init();
        //根据序号升序然后根据日期降序
        this.getQueryWrapper().orderByAsc("sortIndex");
        this.getQueryWrapper().orderByDesc("createTime");
        if (StringUtils.hasText(cnTitle)) {
            cnTitle = cnTitle.replaceAll("%", "////%").replaceAll("_", "////_");
            this.getQueryWrapper().like("cnTitle", cnTitle);
        }
        if (StringUtils.hasText(enTitle)) {
            enTitle = enTitle.replaceAll("%", "////%").replaceAll("_", "////_");
            this.getQueryWrapper().like("enTitle", enTitle);
        }
        return this.getQueryWrapper();
    }
}
