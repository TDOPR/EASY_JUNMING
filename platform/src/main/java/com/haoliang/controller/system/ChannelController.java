package com.haoliang.controller.system;


import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.SysChannel;
import com.haoliang.service.SysChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 渠道管理
 * @author Dominick Li
 **/
@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    private SysChannelService channelService;

    /**
     * 查询所有
     */
    @GetMapping
    @PreAuthorize("hasAuthority('sys:channel:list')")
    public JsonResult<List<SysChannel>> queryByCondition() {
        return channelService.findAll();
    }

    /**
     * 删除
     * @param id 渠道Id
     */
    @OperationLog(module = "渠道管理",description = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('sys:channel:remove')")
    public JsonResult deleteById(@PathVariable Integer id) {
        return channelService.deleteChannelById(id);
    }

    /**
     * 添加或修改
     */
    @OperationLog(module = "渠道管理",description = "添加或修改")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('sys:channel:add','sys:channel:edit')")
    public JsonResult add(@RequestBody SysChannel sysChannel) {
        return channelService.saveChannel(sysChannel);
    }

    /**
     * 刷新渠道层级和顺序
     */
    @OperationLog(module = "渠道管理",description = "刷新渠道层级和顺序")
    @PostMapping("/reload")
    public JsonResult reload(@RequestBody List<SysChannel> sysChannelList){
        return channelService.reload(sysChannelList);
    }

//    /**
//     * 获取当前机构和所有子机构信息
//     * @param id  机构Id
//     */
//    @GetMapping("/getChildChannel/{id}")
//    public JsonResult<SysChannel> getChildChannel(@PathVariable Integer id){
//        return channelService.getChildChannel(id);
//    }
}
