//package com.haoliang.service;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.haoliang.model.ProfitLogs;
//import com.haoliang.model.UserInfoEntity;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
//
//public interface UserInfoService extends IService<UserInfoEntity> {
//
//    //注册用户
//    void createUser(String userAddress,String inviteAddress) throws Exception;
//
//    void insertTreePath(Map<String, Object> map);
//
//    void addTeamPerformanceById(int uid, BigDecimal amount);
//
//    void subtractTeamPerformanceById(int uid, BigDecimal amount);
//
//    List<UserInfoEntity> selectSuperUserList();
//}
