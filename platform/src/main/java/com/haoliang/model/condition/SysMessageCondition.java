package com.haoliang.model.condition;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haoliang.common.base.BaseCondition;
import com.haoliang.model.SysMessage;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author Dominick Li
 * @Description 国际化查询条件
 * @CreateTime 2022/11/7 9:56
 **/
@Data
public class SysMessageCondition extends BaseCondition<SysMessage> {

    /**
     * key名称
     */
    private String keyName;

    /**
     * 中文
     */
    private String zhCn;

    /**
     * 繁体中文
     */
    private String zhTw;

    /**
     * 英文
     */
    private String enUs;

    /**
     * 类型 0=管理端 1=客户端
     */
    private Integer type;

    @Override
    public QueryWrapper buildQueryParam() {
        this.buildBaseQueryWrapper();
        if (type != null) {
            this.getQueryWrapper().eq("type", type);
        }
        if (StringUtils.hasText(keyName)) {
            keyName = keyName.replaceAll("%", "////%").replaceAll("_", "////_");
            this.getQueryWrapper().like("keyName", keyName);
        }
        if (StringUtils.hasText(zhCn)) {
            zhCn = zhCn.replaceAll("%", "////%").replaceAll("_", "////_");
            this.getQueryWrapper().like("zhCn", zhCn);
        }
        if (StringUtils.hasText(zhTw)) {
            zhTw = zhTw.replaceAll("%", "////%").replaceAll("_", "////_");
            this.getQueryWrapper().like("zhTw", zhTw);
        }
        if (StringUtils.hasText(enUs)) {
            enUs = enUs.replaceAll("%", "////%").replaceAll("_", "////_");
            this.getQueryWrapper().like("errorMsg", enUs);
        }
        return this.getQueryWrapper();
    }
}
