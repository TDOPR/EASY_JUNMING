package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.TreePath;
import com.haoliang.model.dto.TreePathAmountDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 */
public interface TreePathMapper extends BaseMapper<TreePath> {

    int insertTreePath( int uid,@Param("pid") int pid);

    List<TreePathAmountDTO> getTreeAmountByUserId(@Param("uid")Integer userId);
}
