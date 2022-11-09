package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.TreePath;
import com.haoliang.model.Wallets;

import java.util.List;
import java.util.Map;

public interface TreePathService extends IService<TreePath> {

    List<Map> getNumByLevel(int uid);
    Map getLevelById(int uid);

    List<Map> getPathById(int uid);

    Map getUserLevelById(int uid);
}
