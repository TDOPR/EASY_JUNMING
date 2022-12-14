package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
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
            return JsonResult.failureResult("?????????????????????????????????????????????!");
        }
        if (metaInfo.getId() == null) {
            //??????
            boolean flag = nativeQueryServer.createTable(metaInfo);
            if (!flag) {
                return JsonResult.failureResult("???????????????!");
            }

            //????????????
            SysMenu sysMenu = new SysMenu();
            sysMenu.setIcon("iconfont icon-rizhiguanli icon-menu");
            Integer maxSortIndex = sysMenuMapper.findMaxSortIndex();
            sysMenu.setSortIndex(maxSortIndex++);
            sysMenu.setPath("/index/universal/" + metaInfo.getId());
            sysMenu.setTitle(metaInfo.getMetaName());
            sysMenu.setImportStr("universal/index.vue");
            sysMenuMapper.insert(sysMenu);
            metaInfo.setMenuId(sysMenu.getId());
            //???????????????????????????
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
            //???????????????????????????????????????????????????
            if (!exists.getMetaName().equals(metaInfo.getMetaName())) {
                SysMenu sysMenu = sysMenuMapper.selectById(metaInfo.getMenuId());
                sysMenu.setTitle(metaInfo.getMetaName());
                sysMenuMapper.updateById(sysMenu);
            }

            //????????????????????????????????????
            Integer sortIndex = 0;
            //????????????????????????????????????id?????????????????????
            Map<Integer, MetaColumn> idColumnMapping = exists.getMetaColumnList().stream().collect(Collectors.toMap(MetaColumn::getId, java.util.function.Function.identity()));

            for (MetaColumn metaColumn : metaInfo.getMetaColumnList()) {
                metaColumn.setSortIndex(sortIndex);
                sortIndex++;
                if (metaColumn.getMetaId() == null) {
                    //????????????
                    metaColumn.setMetaId(metaInfo.getId());
                    //??????sql????????????
                    nativeQueryServer.addColumn(metaInfo.getTableCode(), metaColumn);
                } else {
                    MetaColumn existsColumn = idColumnMapping.remove(metaColumn.getId());
                    if (!metaColumn.getColumnCode().equals(existsColumn.getColumnCode())) {
                        //???????????????????????????,???????????????sql?????????????????????????????????
                        nativeQueryServer.changeColumn(metaInfo.getTableCode(), existsColumn.getColumnCode(), metaColumn);
                    }
                }
            }
            //????????????
            if (idColumnMapping.size() > 0) {
                metaColumnService.removeByIds(idColumnMapping.values());
                for (MetaColumn metaColumn : idColumnMapping.values()) {
                    //??????sql????????????
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
        //??????????????????????????????
        List<Integer> menuIdList = metaInfoList.stream().map(MetaInfo::getMenuId).collect(Collectors.toList());
        //???????????????????????????
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getMenuId, menuIdList));
        //???????????????
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
