package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.utils.DateUtil;
import com.haoliang.enums.DataTypeEnum;
import com.haoliang.enums.RoleTypeEnum;
import com.haoliang.mapper.MetaInfoMapper;
import com.haoliang.mapper.SysMenuMapper;
import com.haoliang.mapper.SysRoleMenuMapper;
import com.haoliang.model.MetaColumn;
import com.haoliang.model.MetaInfo;
import com.haoliang.model.SysMenu;
import com.haoliang.model.SysRoleMenu;
import com.haoliang.model.condition.MetaInfoCondition;
import com.haoliang.model.vo.ViewSelectVO;
import com.haoliang.server.NativeQueryServer;
import com.haoliang.service.MetaColumnService;
import com.haoliang.service.MetaInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author Dominick Li
 * @CreateTime 2022/5/24 22:27
 * @Description
 **/
@Service
public class MetaInfoServiceImpl extends ServiceImpl<MetaInfoMapper, MetaInfo> implements MetaInfoService {

    @Resource
    private MetaInfoMapper metaInfoMapper;

    @Autowired
    private MetaColumnService metaColumnService;

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private NativeQueryServer nativeQueryServer;


    @Override
    public JsonResult saveMetaInfo(MetaInfo metaInfo) {
        MetaInfo exists = this.getOne(new LambdaQueryWrapper<MetaInfo>().eq(MetaInfo::getMetaName, metaInfo.getMetaName()).or().eq(MetaInfo::getTableCode, metaInfo.getTableCode()));
        if (exists != null && !exists.getId().equals(metaInfo.getId())) {
            return JsonResult.failureResult("模型名称和数据库表名称不能重复!");
        }
        if (metaInfo.getId() == null) {
            //新增
            boolean flag = nativeQueryServer.createTable(metaInfo);
            if (!flag) {
                return JsonResult.failureResult("创建表异常!");
            }

            //添加菜单
            SysMenu sysMenu = new SysMenu();
            sysMenu.setIcon("iconfont icon-rizhiguanli icon-menu");
            Integer maxSortIndex = sysMenuMapper.findMaxSortIndex();
            sysMenu.setSortIndex(maxSortIndex++);
            sysMenu.setPath("/index/universal/" + metaInfo.getId());
            sysMenu.setTitle(metaInfo.getMetaName());
            sysMenu.setImportStr("universal/index.vue");
            sysMenuMapper.insert(sysMenu);
            metaInfo.setMenuId(sysMenu.getId());
            //添加角色菜单关联表
            SysRoleMenu sysRoleMenu = new SysRoleMenu(RoleTypeEnum.ADMIN.getCode(), sysMenu.getId(),1);
            sysRoleMenuMapper.insert(sysRoleMenu);
            this.save(metaInfo);

            Integer sortIndex = 0;
            for (MetaColumn metaColumn : metaInfo.getMetaColumnList()) {
                metaColumn.setSortIndex(sortIndex);
                metaColumn.setMetaId(metaInfo.getId());
                sortIndex++;
            }
        } else {
            //如果模型名称变更需同步修改菜单名称
            if (!exists.getMetaName().equals(metaInfo.getMetaName())) {
                SysMenu sysMenu = sysMenuMapper.selectById(metaInfo.getMenuId());
                sysMenu.setTitle(metaInfo.getMetaName());
                sysMenuMapper.updateById(sysMenu);
            }

            //需要删除字段或者添加字段
            Integer sortIndex = 0;
            //获取数据库中已存在的字段id和字段名称映射
            Map<Integer, MetaColumn> idColumnMapping = exists.getMetaColumnList().stream().collect(Collectors.toMap(MetaColumn::getId, java.util.function.Function.identity()));

            for (MetaColumn metaColumn : metaInfo.getMetaColumnList()) {
                metaColumn.setSortIndex(sortIndex);
                sortIndex++;
                if (metaColumn.getMetaId() == null) {
                    //新增字段
                    metaColumn.setMetaId(metaInfo.getId());
                    //执行sql新增字段
                    nativeQueryServer.addColumn(metaInfo.getTableCode(), metaColumn);
                } else {
                    MetaColumn existsColumn = idColumnMapping.remove(metaColumn.getId());
                    if (!metaColumn.getColumnCode().equals(existsColumn.getColumnCode())) {
                        //如果字段名称修改过,则需要执行sql修改数据库中的字段名称
                        nativeQueryServer.changeColumn(metaInfo.getTableCode(), existsColumn.getColumnCode(), metaColumn);
                    }
                }
            }
            //删除字段
            if (idColumnMapping.size() > 0) {
                metaColumnService.removeByIds(idColumnMapping.values());
                for (MetaColumn metaColumn : idColumnMapping.values()) {
                    //执行sql删除字段
                    nativeQueryServer.dropColumn(metaInfo.getTableCode(), metaColumn.getColumnCode());
                }
            }
            this.saveOrUpdate(metaInfo);
        }
        metaColumnService.saveOrUpdateBatch(metaInfo.getMetaColumnList());
        return JsonResult.successResult();
    }

    @Override
    public JsonResult deleteByIdList(List<Long> idList) {
        List<MetaInfo> metaInfoList = this.listByIds(idList);
        for (MetaInfo metaInfo : metaInfoList) {
            nativeQueryServer.dropTable(metaInfo.getTableCode());
        }
        //查询元数据绑定的菜单
        List<Integer> menuIdList = metaInfoList.stream().map(MetaInfo::getMenuId).collect(Collectors.toList());
        //删除角色菜单关联表
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getMenuId, menuIdList));
        //删除菜单表
        sysMenuMapper.deleteBatchIds(menuIdList);
        metaColumnService.remove(new LambdaQueryWrapper<MetaColumn>().in(MetaColumn::getMetaId, idList));
        this.removeByIds(idList);
        return JsonResult.successResult();
    }


    @Override
    public JsonResult getDataTypeList() {
        List<ViewSelectVO> viewSelectVOList = new ArrayList<>();
        for (DataTypeEnum dataTypeEnum : DataTypeEnum.values()) {
            viewSelectVOList.add(new ViewSelectVO(dataTypeEnum.getValue(), dataTypeEnum.getName()));
        }
        return JsonResult.successResult(viewSelectVOList);
    }

    @Override
    public JsonResult findAll(PageParam<MetaInfo, MetaInfoCondition> pageParam) {
        LocalDateTime endDate = pageParam.getSearchParam().getEndDate();
        if (endDate != null) {
            pageParam.getSearchParam().setEndDate(endDate);
        }
        IPage<MetaInfo> page = metaInfoMapper.selectbyPage(pageParam.getPage(),pageParam.getSearchParam().getMetaName(),pageParam.getSearchParam().getBeginDate(),pageParam.getSearchParam().getEndDate());
        return JsonResult.successResult(new PageVO(page));
    }

    @Override
    public MetaInfo getMetaInfo(Long id) {
        MetaInfo metaInfo = this.getById(id);
        List<MetaColumn> metaColumnList = metaColumnService.list(new LambdaQueryWrapper<MetaColumn>().eq(MetaColumn::getMetaId, id));
        metaInfo.setMetaColumnList(metaColumnList);
        return metaInfo;
    }
}
