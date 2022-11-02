package com.haoliang.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.common.utils.StringUtil;
import com.haoliang.enums.RoleTypeEnum;
import com.haoliang.mapper.SysMenuMapper;
import com.haoliang.mapper.SysRoleMapper;
import com.haoliang.mapper.SysRoleMenuMapper;
import com.haoliang.mapper.SysUserMapper;
import com.haoliang.model.SysMenu;
import com.haoliang.model.SysRole;
import com.haoliang.model.SysRoleMenu;
import com.haoliang.model.bo.SysRoleBO;
import com.haoliang.model.vo.RoleVO;
import com.haoliang.model.vo.SelectVO;
import com.haoliang.service.SysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominick Li
 * @CreateTime 2020/10/12 13:51
 * @description
 **/
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Override
    public JsonResult queryByCondition(PageParam<SysRole> pageParam) {
        IPage<SysRole> iPage = this.page(pageParam.getPage(), pageParam.getQueryWrapper());
        List<SysRole> sysRoles = iPage.getRecords();
        List<RoleVO> roleVOList = new ArrayList<>();
        RoleVO roleVO;
        for (SysRole sysRole : sysRoles) {
            roleVO = new RoleVO();
            roleVO.setUserStr(StringUtil.List2Str(sysUserMapper.findAllByUsernameByRoleId(sysRole.getId()), ","));
            BeanUtils.copyProperties(sysRole, roleVO);
            if (RoleTypeEnum.nameOf(sysRole.getRoleCode()) != null) {
                roleVO.setBuiltIn("1");
            } else {
                roleVO.setBuiltIn("0");
            }
            roleVO.setMenuIds(sysRoleMenuMapper.findAllMenuIdByRoleId(sysRole.getId()));
            roleVOList.add(roleVO);
        }
        return JsonResult.successResult(new PageVO(iPage.getTotal(), iPage.getPages(), roleVOList));
    }

    @Override
    public JsonResult saveRole(SysRoleBO sysRole) {
        if (sysRole.getId() == null) {
            //角色名称和编码不能重复
            SysRole exists = this.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleName, sysRole.getRoleName()));
            if (exists != null) {
                return JsonResult.failureResult("角色名称不能重复!");
            }
            exists = this.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, sysRole.getRoleCode()));
            if (exists != null) {
                return JsonResult.failureResult("角色编码不能重复!");
            }
            SysRole newRole = new SysRole();
            BeanUtils.copyProperties(sysRole, newRole);
            this.save(newRole);
            sysRole.setId(newRole.getId());
        } else {
            SysRole oldSysRole = this.getById(sysRole.getId());
            oldSysRole.setRoleCode(sysRole.getRoleCode());
            oldSysRole.setRoleName(sysRole.getRoleName());
            this.saveOrUpdate(oldSysRole);
            sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, sysRole.getId()));
        }

        List<Integer> menuIds = sysRole.getMenuIds();
        if (menuIds != null && menuIds.size() > 0) {
            menuIds.forEach(menuId -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu(sysRole.getId(), menuId);
                sysRoleMenuMapper.insert(sysRoleMenu);
            });
        }
        return JsonResult.successResult();
    }


    @Override
    public List<SysMenu> getMenuListByRole(Integer roleId) {
        List<SysMenu> sysMenuList = sysMenuMapper.findAllByParentIdAndRoleIdOrderBySortIndexAsc(roleId);
        return sysMenuList;
    }

    @Override
    public JsonResult<List<SelectVO>> findSelectList() {
        List<SysRole> sysRoleList = this.list();
        List<SelectVO> selectVOList = new ArrayList<>();
        sysRoleList.forEach(sysRole -> {
            selectVOList.add(new SelectVO(sysRole.getId(), sysRole.getRoleName()));
        });
        return JsonResult.successResult(selectVOList);
    }

    @Override
    public JsonResult getMenuListByToken(String token) {
        String roleCode = JwtTokenUtils.getRoleCodeFromToken(token);
        SysRole sysRole = this.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
        return JsonResult.successResult(getMenuListByRole(sysRole.getId()));
    }
}
