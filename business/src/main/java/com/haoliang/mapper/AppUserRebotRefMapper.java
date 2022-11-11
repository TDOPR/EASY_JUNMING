package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.AppUserRebotRef;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/10 17:55
 **/
public interface AppUserRebotRefMapper extends BaseMapper<AppUserRebotRef> {

    Integer countByUserId(Integer userId);

    AppUserRebotRef findByInviteUserId(Integer inviteUserId);
}
