package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.TreePath;
import com.haoliang.model.dto.TreePathAmountDTO;

import java.util.List;

public interface TreePathService extends IService<TreePath> {

    List<TreePathAmountDTO> getTreeAmountByUserId(Integer userId);

    List<TreePath> getTreePathByUserId(Integer userId);

    List<TreePath> getThreeAlgebraTreePathByUserId(Integer userId);

    Integer countByAncestor(Integer userId);

    void insertTreePath(Integer id, Integer inviteUserId);
}
