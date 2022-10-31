package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    List<Integer> findAllByMenuId(@Param("idList")List<Integer> menuIdList);

    List<Integer> findAllMenuIdByRoleId(@Param("roleId")Integer id);
}
