package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.UpdateUserLevelJob;
import com.haoliang.model.dto.AppUserTreeDTO;

import java.util.List;

public interface UpdateUserLevelTaskMapper extends BaseMapper<UpdateUserLevelJob> {

    List<AppUserTreeDTO> findUserTreeByUserId(Integer userId);

}
