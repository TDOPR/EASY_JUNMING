package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.SysMenu;
import com.haoliang.model.vo.RouterVO;

import java.util.List;

/**
 * @author MCQ
 * @Description
 * @date 2021-04-15 14:53
 */
public interface SysMenuService extends IService<SysMenu> {

    JsonResult findAll();

    JsonResult<RouterVO> findAllByRoleId(Integer roleId);

    JsonResult<RouterVO> findAllByToken(String  token);

    JsonResult reloadMenu(List<SysMenu> sysMenus, String token);

    JsonResult deleteById(Integer id);

}
