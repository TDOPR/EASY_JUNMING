package com.haoliang.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.SysChannel;

import java.util.List;

/**
 * @author Dominick Li
 * @CreateTime 2020/10/13 16:30
 * @description
 **/
public interface SysChannelService extends IService<SysChannel> {

    JsonResult findAll();

    JsonResult saveChannel(SysChannel sysChannel);

    JsonResult reload(List<SysChannel> sysChannelList);

    JsonResult getChildChannel(Integer id);

    JsonResult deleteChannelById(Integer id);
}
