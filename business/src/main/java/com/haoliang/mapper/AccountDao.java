package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.AccountEntity;
import com.haoliang.model.TreePath;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface AccountDao extends BaseMapper<AccountEntity> {

    int addAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId);

    int addSuperAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId);

    int subtractAmount(@Param("userId") long userId, @Param("coinId") long coinId, @Param("amount") BigDecimal amount);

    @MapKey("id")
    int subtractAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount);

    @MapKey("id")
    List<Map> selectStaticIncomeUserList();

    int addPendingAmountByAid(@Param("accountId") long accountId, @Param("amount") BigDecimal amount, @Param("coinId") long coinId);

    @MapKey("id")
    List<Map> selectSuperUserList();

    int selectAllAmount();

    int selectUserRechargeAmount(int uid);
}
