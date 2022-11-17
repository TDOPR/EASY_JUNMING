package com.haoliang.service;

import com.haoliang.common.model.JsonResult;
import com.haoliang.model.dto.RobotDTO;

public interface RobotService {

    JsonResult getRobotList(String token);

    JsonResult buyRebot(RobotDTO robotDTO, String token);

    JsonResult upgradeRebot(RobotDTO robotDTO, String token);
}
