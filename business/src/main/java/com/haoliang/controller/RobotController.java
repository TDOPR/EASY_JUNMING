package com.haoliang.controller;

import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.model.dto.RobotDTO;
import com.haoliang.model.vo.RobotDetailVO;
import com.haoliang.service.RobotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Dominick Li
 * @Description 机器人
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
    public JsonResult<RobotDetailVO> getRobotList(@RequestHeader(JwtTokenUtils.TOKEN_NAME)String token){
        return robotService.getRobotList(token);
    }

    /**
     * 购买机器人
     */
    @PostMapping("/buyRobot")
    public JsonResult buyRebot(@RequestBody RobotDTO robotDTO, @RequestHeader(JwtTokenUtils.TOKEN_NAME)String token){
        return robotService.buyRebot(robotDTO,token);
    }

    /**
     * 升级机器人等级
     */
    @PostMapping("/upgradeRobot")
    public JsonResult upgradeRebot(@RequestBody RobotDTO robotDTO, @RequestHeader(JwtTokenUtils.TOKEN_NAME)String token){
        return robotService.upgradeRebot(robotDTO,token);
    }
}
