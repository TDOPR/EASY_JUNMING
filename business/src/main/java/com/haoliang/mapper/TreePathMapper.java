package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.TreePath;
import com.haoliang.model.dto.TreePathAmountDTO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * @author Administrator
 */
public interface TreePathMapper extends BaseMapper<TreePath> {

    int insertTreePath( int uid,@Param("pid") int pid);

    List<TreePathAmountDTO> getAllAmountByUserId(@Param("uid")Integer userId);

    List<TreePathAmountDTO> getProfitAmountByUserIdAndLevelList(@Param("uid")Integer userId,@Param("localDate") LocalDate localDate,@Param("levelList") List<Integer> levelList);

    BigDecimal getMinTeamTotalProfitAmount(@Param("uid")Integer userId, @Param("localDate") LocalDate localDate);

    BigDecimal getTeamTotalProfitAmount(@Param("uid")Integer userId, @Param("localDate") LocalDate localDate);
}
