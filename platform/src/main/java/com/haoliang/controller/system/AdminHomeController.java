package com.haoliang.controller.system;

import com.haoliang.common.model.JsonResult;
import com.haoliang.model.vo.AdminHomeVO;
import com.haoliang.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  系统主页
 * @author Dominick Li
 * @CreateTime 2022/11/23 15:41
 **/
@RestController
@RequestMapping("/admin/home")
public class AdminHomeController {

    @Autowired
    private SystemService systemService;

    /**
     * 获取主页数据
     * @return
     */
    @GetMapping
    public JsonResult<AdminHomeVO> getHomeInfo() {
        return systemService.getHomeInfo();
    }

}

