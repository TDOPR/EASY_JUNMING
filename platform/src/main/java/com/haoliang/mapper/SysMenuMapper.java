package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.SysMenu;
import com.haoliang.model.vo.MenuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findAllByParentIdOrderBySortIndexAsc();

    List<MenuVO> findAllByParentIdAndRoleIdOrderBySortIndexAsc(@Param("roleId")Integer roleId);

    String findMenuNameById(@Param("menuId")Integer menuId);

    Integer findMaxSortIndex();

    List<String> findAllAuthorityByRoleId(@Param("roleId")Integer roleId);
}
