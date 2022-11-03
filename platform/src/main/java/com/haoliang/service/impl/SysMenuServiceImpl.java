package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.mapper.SysMenuMapper;
import com.haoliang.mapper.SysRoleMenuMapper;
import com.haoliang.model.SysMenu;
import com.haoliang.model.SysRole;
import com.haoliang.model.vo.MenuVO;
import com.haoliang.model.vo.RouterVO;
import com.haoliang.service.SysMenuService;
import com.haoliang.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @date 2021-04-15 14:53
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Override
    public JsonResult findAll() {
        return JsonResult.successResult(sysMenuMapper.findAllByParentIdOrderBySortIndexAsc());
    }

    @Override
    public JsonResult<RouterVO> findAllByRoleId(Integer roleId) {
        RouterVO routerVO = new RouterVO();
        List<MenuVO> sysMenus = sysMenuMapper.findAllByParentIdAndRoleIdOrderBySortIndexAsc(roleId);
        List<String> authorityList=sysMenuMapper.findAllAuthorityByRoleId(roleId);
        routerVO.setMenuList(sysMenus);
        routerVO.setAuthorityList(authorityList);
        return JsonResult.successResult(routerVO);
    }




    @Override
    public JsonResult reloadMenu(List<SysMenu> sysMenuList, String token) {
        SysMenu curSysMenu, subSysMenu;
        List<SysMenu> saveSysMenuList = new ArrayList<>();
        for (int i = 1; i <= sysMenuList.size(); i++) {
            curSysMenu = sysMenuList.get(i - 1);
            curSysMenu.setParentId(0);
            curSysMenu.setSortIndex(i);
            saveSysMenuList.add(curSysMenu);
            for (int j = 1; j <= curSysMenu.getChildren().size(); j++) {
                subSysMenu = curSysMenu.getChildren().get(j - 1);
                subSysMenu.setSortIndex(j);
                subSysMenu.setParentId(curSysMenu.getId());
                saveSysMenuList.add(subSysMenu);
            }
        }
        this.saveOrUpdateBatch(saveSysMenuList);
        String roleCode = JwtTokenUtils.getRoleCodeFromToken(token);
        return JsonResult.successResult(this.findAllByRoleId(sysRoleService.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode)).getId()));
    }


    @Override
    public JsonResult deleteById(Integer id) {
        Integer existsId = sysRoleMenuMapper.findAllByMenuId(id);
        if (existsId != null) {
            String menuName = sysMenuMapper.findMenuNameById(existsId);
            return JsonResult.failureResult("[" + menuName + "]已被角色使用,不能删除!");
        } else {
            this.removeById(id);
            return JsonResult.successResult();
        }

    }

    @Override
    public JsonResult<RouterVO> findAllByToken(String token) {
        SysRole sysRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, JwtTokenUtils.getRoleCodeFromToken(token)));
        return this.findAllByRoleId(sysRole.getId());
    }
}
