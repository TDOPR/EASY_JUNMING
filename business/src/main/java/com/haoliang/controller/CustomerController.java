package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.model.AppUsers;
import com.haoliang.model.condition.AppUsersCondition;
import com.haoliang.model.vo.AppUsersVO;
import com.haoliang.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Dominick Li
 * @Description 客户管理
 * @CreateTime 2022/11/11 18:50
 **/
@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private AppUserService appUserService;

    /**
     * 分页查询
     */
    @PostMapping("/pagelist")
    @PreAuthorize("hasAuthority('sys:customer:list')")
    public JsonResult<PageVO<AppUsersVO>> pageList(@RequestBody PageParam<AppUsers, AppUsersCondition> pageParam) {
        return appUserService.pageList(pageParam);
    }

}
