package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.model.AppUsers;
import com.haoliang.model.condition.AppUsersCondition;
import com.haoliang.model.dto.AppUserRebotDTO;
import com.haoliang.model.vo.AppUsersVO;
import org.apache.ibatis.annotations.Param;

public interface AppUserMapper extends BaseMapper<AppUsers> {

    Integer findInviteIdByUserId(Integer userId);

    IPage<AppUsersVO> page(IPage<AppUsers> page,@Param("param") AppUsersCondition searchParam);

    Integer getValidUserCountByInviteId(Integer userId);

    AppUserRebotDTO getRobotDetailByUserId(Integer userId);
}
