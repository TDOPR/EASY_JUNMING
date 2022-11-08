package com.haoliang.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.bo.IntIdListBO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.model.SysMessage;
import com.haoliang.model.SysNotice;
import com.haoliang.model.condition.SysNoticeCondition;
import com.haoliang.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 公告管理
 * @author Dominick Li
 * @CreateTime 2022/11/7 11:18
 **/
@RestController
@RequestMapping("/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService sysNoticeService;

    /**
     * 分页查询
     */
    @PostMapping("/pagelist")
    @PreAuthorize("hasAuthority('sys:notice:list')")
    public JsonResult<PageVO<SysNotice>> queryByCondition(@RequestBody PageParam<SysNotice, SysNoticeCondition> pageParam) {
        if(pageParam.getSearchParam()==null){
            pageParam.setSearchParam(new SysNoticeCondition());
        }
        IPage<SysMessage> iPage = sysNoticeService.page(pageParam.getPage(), pageParam.getSearchParam().buildQueryParam());
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    /**
     * 批量删除
     * @param idList Id数组
     */
    @OperationLog(module = "公告管理", description = "批量删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:notice:remove')")
    public JsonResult deleteByIds(@RequestBody IntIdListBO intIdListBO) {
        return JsonResult.build(sysNoticeService.removeByIds(intIdListBO.getIdList()));
    }

    /**
     * 新增或修改
     */
    //@OperationLog(module = "公告管理", description = "新增或修改")
    @PostMapping
    @PreAuthorize("hasAnyAuthority('sys:notice:add','sys:notice:edit')")
    public JsonResult save(@Valid @RequestBody SysNotice sysNotice) {
        return JsonResult.build(sysNoticeService.saveOrUpdate(sysNotice));
    }

}
