package com.haoliang.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.annotation.OperationLog;
import com.haoliang.common.constant.OperationAction;
import com.haoliang.common.constant.OperationModel;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.dto.IntIdListDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.model.SysMessage;
import com.haoliang.model.SysNotice;
import com.haoliang.model.condition.SysNoticeCondition;
import com.haoliang.model.vo.SysNoticeVO;
import com.haoliang.service.SysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    @OperationLog(module = OperationModel.SYS_NOTICE, description = OperationAction.REMOVE)
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('sys:notice:remove')")
    public JsonResult deleteByIds(@RequestBody IntIdListDTO intIdListDTO) {
        return JsonResult.build(sysNoticeService.removeByIds(intIdListDTO.getIdList()));
    }

    /**
     * 新增或修改
     */
    @OperationLog(module = OperationModel.SYS_NOTICE, description = OperationAction.REMOVE)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('sys:notice:add','sys:notice:edit')")
    public JsonResult save(@Valid @RequestBody SysNotice sysNotice) {
        return sysNoticeService.saveNotice(sysNotice);
    }

    /**
     * 根据用户Id获取公告列表
     * @param type 1=中文 0=英文
     */
    @GetMapping("/{type}")
    public JsonResult<List<SysNoticeVO>> findMyNoticeList(@RequestHeader(JwtTokenUtil.TOKEN_NAME)String token, @PathVariable Integer type){
        return sysNoticeService.findMyNoticeList(token,type);
    }

    /**
     * 删除用户关联的公告消息
     * @param id 公告Id
     */
    @DeleteMapping("/{id}")
    public JsonResult deleteUserNoticeById(@RequestHeader(JwtTokenUtil.TOKEN_NAME)String token,@PathVariable Integer id){
        return sysNoticeService.deleteUserNoticeById(token,id);
    }


}
