package com.haoliang.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.mapper.SysChannelMapper;
import com.haoliang.model.SysChannel;
import com.haoliang.service.SysChannelService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Dominick Li
 * @CreateTime 2020/10/12 17:58
 * @description
 **/
@Service
public class SysChannelServiceImpl extends ServiceImpl<SysChannelMapper, SysChannel> implements SysChannelService {

    @Resource
    private SysChannelMapper sysChannelMapper;

    @Override
    public JsonResult saveChannel(SysChannel sysChannel) {
        if (sysChannel.getId() != null) {
            SysChannel one = this.getById(sysChannel.getId());
            one.setChannelCode(sysChannel.getChannelCode());
            one.setChannelName(sysChannel.getChannelName());
            sysChannel = one;
        } else {
            SysChannel exists = this.getOne(new LambdaQueryWrapper<SysChannel>().eq(SysChannel::getChannelName, sysChannel.getChannelName()));
            if (exists != null) {
                return JsonResult.failureResult("机构名称不能重复!");
            }
            exists = this.getOne(new LambdaQueryWrapper<SysChannel>().eq(SysChannel::getChannelCode, sysChannel.getChannelCode()));
            if (exists != null) {
                return JsonResult.failureResult("机构编码不能重复!");
            }
        }
        this.saveOrUpdate(sysChannel);
        return JsonResult.successResult();
    }

    @Override
    public JsonResult findAll() {
        return JsonResult.successResult(sysChannelMapper.findAllByParentIdIsNullOrderBySortIndex());
    }

    @Override
    public JsonResult reload(List<SysChannel> sysChannelList) {
        SysChannel curSysChannel;
        List<SysChannel> saveSysChannelList = new ArrayList<>();
        for (int i = 1; i <= sysChannelList.size(); i++) {
            curSysChannel = sysChannelList.get(i - 1);
            curSysChannel.setSortIndex(i);
            recursionChannel(saveSysChannelList, curSysChannel);
            saveSysChannelList.add(curSysChannel);
        }
        this.saveOrUpdateBatch(saveSysChannelList);
        return JsonResult.successResult();
    }

    @Override
    public JsonResult getChildChannel(Integer id) {
        SysChannel sysChannel = this.getById(id);
        return JsonResult.successResult(Arrays.asList(sysChannel));
    }

    private void recursionChannel(List<SysChannel> sysChannelList, SysChannel curSysChannel) {
        if (curSysChannel.getChildren().size() == 0) {
            return;
        }
        SysChannel subSysChannel;
        for (int j = 1; j <= curSysChannel.getChildren().size(); j++) {
            subSysChannel = curSysChannel.getChildren().get(j - 1);
            subSysChannel.setSortIndex(j);
            subSysChannel.setParentId(curSysChannel.getId());
            recursionChannel(sysChannelList, subSysChannel);
            sysChannelList.add(subSysChannel);
        }
    }

    @Override
    @Transactional
    //TODO 待优化
    public JsonResult deleteChannelById(Integer id) {
        SysChannel sysChannel = this.getById(id);
        recursionDelete(sysChannel);
        sysChannel.setChildren(null);
        this.removeById(sysChannel.getId());
        return JsonResult.successResult();
    }

    private void recursionDelete(SysChannel sysChannel) {
        if (Collections.isEmpty(sysChannel.getChildren())) {
            return;
        }
        for (SysChannel subSysChannel : sysChannel.getChildren()) {
            recursionDelete(subSysChannel);
            this.removeById(subSysChannel.getId());
        }
    }

}
