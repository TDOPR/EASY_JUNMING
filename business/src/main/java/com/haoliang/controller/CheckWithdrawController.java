package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.model.AppUserWithdraw;
import com.haoliang.model.condition.AppUserWithdrawCondition;
import com.haoliang.model.dto.AuditCheckDTO;
import com.haoliang.model.vo.AppUserWithdrawVO;
import com.haoliang.service.AppUserWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dominick Li
 * @Description 审核提现
 * @CreateTime 2022/11/11 11:44
 **/
@RestController
@RequestMapping("/auditWithdrawal")
public class CheckWithdrawController {

    @Autowired
    private AppUserWithdrawService appUserWithdrawService;

    /**
     * 分页查询
     */
    @PostMapping("/pagelist")
    @PreAuthorize("hasAuthority('sys:examine:list')")
    public JsonResult<PageVO<AppUserWithdrawVO>> pageList(@RequestBody PageParam<AppUserWithdraw, AppUserWithdrawCondition> pageParam) {
        return appUserWithdrawService.pageList(pageParam);
    }

    /**
     * 校验
     */
    @PostMapping("/check")
    @PreAuthorize("hasAuthority('sys:examine:list')")
    public JsonResult check(@RequestBody AuditCheckDTO auditCheckDTO){
        return appUserWithdrawService.check(auditCheckDTO);
    }
}
