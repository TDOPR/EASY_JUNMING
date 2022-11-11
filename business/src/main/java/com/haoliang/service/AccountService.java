//package com.haoliang.service;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.haoliang.enums.BusinessTypeEnum;
//import com.haoliang.model.AccountEntity;
//import com.haoliang.model.ProfitLogs;
//import com.haoliang.model.UserInfoEntity;
//import org.apache.ibatis.annotations.Param;
//
//import javax.security.auth.login.AccountException;
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
//
//public interface AccountService extends IService<AccountEntity> {
//
//    void addAccount(UserInfoEntity userInfoEntity);
//
//    int addAmount(long userId, long coinId, BigDecimal amount);
//
//    boolean addAmount(long userId,
//                      long coinId,
//                      BigDecimal amount,
//                      BusinessTypeEnum businessTypeEnum,
//                      long orderId) throws AccountException;
//
//    int subtractAmount(long userId, long coinId, BigDecimal amount);
//
//    boolean subtractAmount(long userId,
//                           long coinId,
//                           BigDecimal amount,
//                           BusinessTypeEnum businessTypeEnum,
//                           long orderId) throws AccountException;
//
//    AccountEntity queryByUserIdAndCoinId(long userId, long coinId);
//
//    List<Map> selectStaticIncomeUserList();
//
//    int addAmountByAid(long accountId, BigDecimal amount, long coinId);
//
//    int addSuperAmountByAid(long accountId, BigDecimal amount, long coinId);
//
//    int addPendingAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId);
//    List<Map> selectSuperUserList();
//
//    int selectAllAmount();
//
//    int selectUserRechargeAmount(int uid);
//}
