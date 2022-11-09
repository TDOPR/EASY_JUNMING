package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.TreePath;
import com.haoliang.model.WalletLogs;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface TreePathMapper extends BaseMapper<TreePath> {

    @MapKey("uid")
    List<Map> getNumByLevel(@Param("uid") int uid);

    @MapKey("uid")
    Map getLevelById(@Param("uid") int uid);

    @MapKey("ancestor")
    List<Map> getPathById(int uid);

    @MapKey("id")
    Map getUserLevelById(int uid);
}
