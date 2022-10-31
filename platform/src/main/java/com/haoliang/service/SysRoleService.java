package com.haoliang.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.PageParam;
import com.haoliang.model.bo.SysRoleBO;
import com.haoliang.model.SysMenu;
import com.haoliang.model.SysRole;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.vo.SelectVO;

import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    List<SysMenu> getMenuListByRole(Integer roleId);

    JsonResult<List<SelectVO>> findSelectList();

    JsonResult getMenuListByToken(String token);

    JsonResult queryByCondition(PageParam<SysRole> pageParam);

    JsonResult saveRole(SysRoleBO sysRoleBO);
}
