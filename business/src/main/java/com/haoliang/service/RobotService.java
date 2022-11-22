package com.haoliang.service;

import com.haoliang.common.model.JsonResult;
import com.haoliang.model.dto.RobotDTO;

public interface RobotService {

    /**
     * 获取机器人列表
     * @param token 身份凭证
     * @return
     */
    JsonResult getRobotList(String token);

    /**
     * 购买机器人
     * @param robotDTO 机器人信息
     * @param token 身份凭证
     * @return
     */
    JsonResult buyRebot(RobotDTO robotDTO, String token);

    /**
     * 升级机器人
     * @param robotDTO 机器人信息
     * @param token 身份凭证
     * @return
     */
    JsonResult upgradeRebot(RobotDTO robotDTO, String token);
}
