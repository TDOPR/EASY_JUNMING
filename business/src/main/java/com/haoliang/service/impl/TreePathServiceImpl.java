package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.mapper.TreePathMapper;
import com.haoliang.model.TreePath;
import com.haoliang.model.dto.TreePathAmountDTO;
import com.haoliang.service.TreePathService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    public List<TreePath> getThreeAlgebraTreePathByUserId(Integer userId) {
        return this.list(new LambdaQueryWrapper<TreePath>().select(TreePath::getDescendant, TreePath::getLevel).eq(TreePath::getAncestor, userId).in(TreePath::getLevel, EasyTradeConfig.ALGEBRA_LEVEL));
    }

    @Override
    public List<TreePath> getTreePathByUserId(Integer userId) {
        return this.list(new LambdaQueryWrapper<TreePath>().select(TreePath::getDescendant, TreePath::getLevel).eq(TreePath::getAncestor, userId));
    }

    @Override
    public List<TreePathAmountDTO> getAllAmountByUserId(Integer userId) {
        return treePathMapper.getAllAmountByUserId(userId);
    }

    @Override
    public Integer countByAncestor(Integer userId) {
        return (int) this.count(new LambdaQueryWrapper<TreePath>().eq(TreePath::getAncestor, userId));
    }

    @Override
    public void insertTreePath(Integer id, Integer inviteUserId) {
         treePathMapper.insertTreePath(id, inviteUserId);
    }

    @Override
    public List<TreePathAmountDTO> getProfitAmountByUserIdAndLevelList(Integer userId, LocalDate localDate, List<Integer> levelList) {
        return treePathMapper.getProfitAmountByUserIdAndLevelList(userId,localDate,levelList);
    }

    @Override
    public BigDecimal getMinTeamTotalProfitAmount(Integer userId,LocalDate localDate) {
        return treePathMapper.getMinTeamTotalProfitAmount(userId,localDate);
    }

    @Override
    public BigDecimal getTeamTotalProfitAmount(Integer userId, LocalDate localDate) {
        return treePathMapper.getTeamTotalProfitAmount(userId,localDate);
    }

}
