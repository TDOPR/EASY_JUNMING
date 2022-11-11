//
//package com.haoliang.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.haoliang.model.TreePath;
//import com.haoliang.model.UserInfoEntity;
//import org.apache.ibatis.annotations.MapKey;
//import org.apache.ibatis.annotations.Param;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Administrator
// */
//public interface UserInfoDao extends BaseMapper<UserInfoEntity> {
//
//    void insertTreePath(Map<String, Object> map);
//
//    void addTeamPerformanceById(@Param("amount") BigDecimal amount, @Param("uid") int uid);
//
//    void subtractTeamPerformanceById(@Param("amount") BigDecimal amount, @Param("uid") int uid);
//
//    List<UserInfoEntity> selectSuperUserList();
//
//    void addPowerAndPowerLimitByAddress(@Param("power") BigDecimal power, @Param("powerLimit") BigDecimal powerLimit, @Param("address") String address);
//
//    void addPowerById(@Param("power") BigDecimal power, @Param("id") int id);
//
//    void addLpPowerById(@Param("power") BigDecimal power, @Param("id") int id);
//
//    void subLpPowerById(@Param("power") BigDecimal power, @Param("id") int id);
//
//    void updateLpPowerById(@Param("power") BigDecimal power, @Param("id") int id);
//
//    void addPowerOverflowById(@Param("power") BigDecimal power, @Param("id") int id);
//
//    void updatePowerOverflowById(@Param("power") BigDecimal power, @Param("id") int id);
//
//    void updatePowerById(@Param("power") BigDecimal power, @Param("id") int id);
//
//    void addLpTeamPerformanceById(@Param("lpTeamPerformance") BigDecimal teamPerformance, @Param("id") int id);
//
//    void subLpTeamPerformanceById(@Param("lpTeamPerformance") BigDecimal teamPerformance, @Param("id") int id);
//
//    void addLpTeamSmallPerformanceById(@Param("lpTeamSmallPerformance") BigDecimal lpTeamSmallPerformance, @Param("id") int id);
//
//    void subLpTeamSmallPerformanceById(@Param("lpTeamSmallPerformance") BigDecimal lpTeamSmallPerformance, @Param("id") int id);
//
//    void updateBigUidById(@Param("bigUid") int bigUid, @Param("id") int id);
//
//    void updateLpTeamPerformanceById(@Param("lpTeamPerformance") BigDecimal teamPerformance, @Param("id") int id);
//
//    void updateLpTeamSmallPerformanceById(@Param("lpTeamSmallPerformance") BigDecimal lpTeamSmallPerformance, @Param("id") int id);
//}
