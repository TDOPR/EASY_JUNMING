package com.haoliang.service;

import com.haoliang.common.model.JsonResult;
import com.haoliang.model.dto.RobotDTO;

public interface RobotService {

    /**
     * 获取机器人列表
     */
    JsonResult getRobotList();

    /**
     * 购买机器人
     * @param robotDTO 机器人信息
     */
    JsonResult buyRebot(RobotDTO robotDTO);

    /**
     * 升级机器人
     * @param robotDTO 机器人信息
     */
    JsonResult upgradeRebot(RobotDTO robotDTO);
}
