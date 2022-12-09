package com.haoliang.controller;

import com.haoliang.common.annotation.RepeatSubmit;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.dto.RobotDTO;
import com.haoliang.model.vo.RobotDetailVO;
import com.haoliang.service.RobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 机器人
 * @author Dominick Li
 * @CreateTime 2022/11/17 10:54
 **/
@RestController
@RequestMapping("/robot")
public class RobotController {


    @Autowired
    private RobotService robotService;

    /**
     * 获取机器人列表
     */
    @GetMapping
    public JsonResult<RobotDetailVO> getRobotList(){
        return robotService.getRobotList();
    }

    /**
     * 购买机器人
     */
    @RepeatSubmit
    @PostMapping("/buyRobot")
    public JsonResult buyRebot(@RequestBody RobotDTO robotDTO){
        return robotService.buyRebot(robotDTO);
    }

    /**
     * 升级机器人等级
     */
    @RepeatSubmit
    @PostMapping("/upgradeRobot")
    public JsonResult upgradeRebot(@RequestBody RobotDTO robotDTO){
        return robotService.upgradeRebot(robotDTO);
    }
}
