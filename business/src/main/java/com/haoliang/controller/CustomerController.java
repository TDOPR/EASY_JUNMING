package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.model.AppUsers;
import com.haoliang.model.condition.AppUsersCondition;
import com.haoliang.model.dto.CustomerWalletLogsDTO;
import com.haoliang.model.vo.AppUsersVO;
import com.haoliang.service.AppUserService;
import com.haoliang.service.WalletLogsService;
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

    @Autowired
    private WalletLogsService walletLogsService;

    /**
     * 分页查询
     */
    @PostMapping("/pagelist")
    @PreAuthorize("hasAuthority('sys:customer:list')")
    public JsonResult<PageVO<AppUsersVO>> pageList(@RequestBody PageParam<AppUsers, AppUsersCondition> pageParam) {
        return appUserService.pageList(pageParam);
    }

    /**
     * 用户提出到钱包记录查询
     */
    @PostMapping("/walletLogs")
    @PreAuthorize("hasAuthority('sys:customer:list')")
    public JsonResult bringToWallet(@RequestBody CustomerWalletLogsDTO walletLogsDTO){
        return  walletLogsService.listByUserIdAndType(walletLogsDTO.getUserId(), walletLogsDTO.getType());
    }

}
