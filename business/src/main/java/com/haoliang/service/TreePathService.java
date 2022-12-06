package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.model.TreePath;
import com.haoliang.model.dto.TreePathAmountDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TreePathService extends IService<TreePath> {

    /**
     * 获取供应商团队最近几代的量化收益
     * @param userId 供应商Id
     * @param localDate 日期
     * @param levelList 包含的代数
     * @return
     */
    List<TreePathAmountDTO> getProfitAmountByUserIdAndLevelList(Integer userId, LocalDate localDate, List<Integer> levelList);

    /**
     * 获取供应商的团队业绩
     * @param userId
     * @return
     */
    List<TreePathAmountDTO> getAllAmountByUserId(Integer userId);


    /**
     * 获取代理商的团队数据
     * @param userId
     * @return
     */
    @Deprecated
    List<TreePath> getTreePathByUserId(Integer userId);

    /**
     * 获取三代的用户关系数据
     * @param userId
     * @return
     */
    @Deprecated
    List<TreePath> getThreeAlgebraTreePathByUserId(Integer userId);

    /**
     * 查询团队总数据 (包含供应商自己)
     * @param userId 供应商Id
     */
    Integer countByAncestor(Integer userId);

    /**
     * 插入供应商 团队数据
     * @param userId 用户Id
     * @param inviteUserId 邀请人的用户Id
     */
    void insertTreePath(Integer userId, Integer inviteUserId);

    /**
     * 获取小团队今天量化总收益
     * @param userId 供应商Id
     * @param localDate 日期
     * @return 总金额
     */
    BigDecimal getMinTeamTotalProfitAmount(Integer userId,LocalDate localDate);

    /**
     * 获取所有团队今天量化总收益
     * @param userId 供应商Id
     * @param localDate 日期
     * @return 总金额
     */
    BigDecimal getTeamTotalProfitAmount(Integer userId, LocalDate localDate);
}
