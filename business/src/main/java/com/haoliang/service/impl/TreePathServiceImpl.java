package com.haoliang.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.mapper.TreePathMapper;
import com.haoliang.mapper.WalletMapper;
import com.haoliang.model.TreePath;
import com.haoliang.model.Wallets;
import com.haoliang.service.TreePathService;
import com.haoliang.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 12:20
 **/
@Service
public class TreePathServiceImpl extends ServiceImpl<TreePathMapper, TreePath> implements TreePathService {

    @Resource
    private TreePathMapper treePathMapper;

    @Override
    public List<Map> getNumByLevel(int uid) {
        return treePathMapper.getNumByLevel(uid);
    }

    @Override
    public Map getLevelById(int uid) {
        return treePathMapper.getLevelById(uid);
    }

    @Override
    public List<Map> getPathById(int uid){
        return treePathMapper.getPathById(uid);
    }

    @Override
    public Map getUserLevelById(int uid){
        return treePathMapper.getUserLevelById(uid);
    }
}
