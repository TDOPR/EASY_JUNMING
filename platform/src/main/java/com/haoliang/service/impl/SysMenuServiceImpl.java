package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.enums.RoleTypeEnum;
import com.haoliang.mapper.SysMenuMapper;
import com.haoliang.mapper.SysRoleMenuMapper;
import com.haoliang.model.SysMenu;
import com.haoliang.model.SysRole;
import com.haoliang.model.dto.MenuIdDTO;
import com.haoliang.model.vo.MenuVO;
import com.haoliang.model.vo.RouterVO;
import com.haoliang.service.SysMenuService;
import com.haoliang.service.SysRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @date 2021-04-15 14:53
 */
@Slf4j
@Service
@CacheConfig(cacheNames = CacheKeyPrefixConstants.SYS_MENU)
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysRoleService sysRoleService;

    //@Cacheable(key = "'findAll'")
    @Override
    public JsonResult findAll() {
        return JsonResult.successResult(sysMenuMapper.findAllByParentIdOrderBySortIndexAsc());
    }

    @Cacheable(key = "#roleId")
    @Override
    public JsonResult<RouterVO> findAllByRoleId(Integer roleId) {
        log.info("缓存未命中,查询菜单权限数据并缓存");
        RouterVO routerVO = new RouterVO();
        List<MenuVO> sysMenus;
        List<String> authorityList;
        if (roleId.equals(RoleTypeEnum.ADMIN.getCode())) {
            sysMenus = sysMenuMapper.findAllByParentIOrderBySortIndexAsc();
            authorityList = sysMenuMapper.findAllAuthority();
        } else {
            sysMenus = sysMenuMapper.findAllByParentIdAndRoleIdOrderBySortIndexAsc(roleId);
            authorityList = sysMenuMapper.findAllAuthorityByRoleId(roleId);
        }
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
        return JsonResult.successResult(this.findAllByRoleCode(JwtTokenUtil.getRoleCodeFromToken(token)));
    }


    @Override
    @CacheEvict(allEntries = true)
    public JsonResult deleteByIdList(List<Integer> idList) {
        Integer id = idList.get(0);
        Integer existsId = sysRoleMenuMapper.findAllByMenuIdIn(id);
        //只能删除单个
        if (existsId != null) {
            return JsonResult.failureResult(ReturnMessageEnum.MENU_EXISTS_USE);
        } else {
            //根据id查询子菜单Id
            List<MenuIdDTO> menuIdDTOList = sysMenuMapper.findIdByParentId(id);
            List<Integer> allId = new ArrayList<>();
            allId.add(id);
            for (MenuIdDTO menuIdDTO : menuIdDTOList) {
                recursionId(menuIdDTO, allId);
            }
            this.removeByIds(allId);
            return JsonResult.successResult();
        }

    }

    /**
     * 递归获取树节点的所有Id
     *
     * @param menuIdDTO 当前节点
     * @param allId     id存储容器
     */
    private void recursionId(MenuIdDTO menuIdDTO, List<Integer> allId) {
        allId.add(menuIdDTO.getId());
        for (MenuIdDTO child : menuIdDTO.getChildren()) {
            recursionId(child, allId);
        }
    }

    @Override
    public JsonResult<RouterVO> findAllByRoleCode(String roleCode) {
        SysRole sysRole = sysRoleService.getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, roleCode));
        return this.findAllByRoleId(sysRole.getId());
    }

    @Override
    @CacheEvict(allEntries = true)
    public JsonResult saveMenu(SysMenu sysMenu) {
        return JsonResult.build(this.saveOrUpdate(sysMenu));
    }

    @Override
    public JsonResult getTree() {
        return JsonResult.successResult(sysMenuMapper.getTree());
    }

    @Cacheable(key = "'authority:' +#roleId")
    @Override
    public List<String> findAuthorityByRoleId(Integer roleId) {
        log.info("缓存未命中,查询权限数据并缓存");
        List<String> authorityList;
        if (roleId.equals(RoleTypeEnum.ADMIN.getCode())) {
            authorityList = sysMenuMapper.findAllAuthority();
        } else {
            authorityList = sysMenuMapper.findAllAuthorityByRoleId(roleId);
        }
        return authorityList;
    }

    @Override
    @CacheEvict(allEntries = true)
    public void updateRoleMenu() {
        log.info("reload  authority cache");
    }
}
