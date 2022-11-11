//package com.haoliang.mapper;
//
//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
//import com.haoliang.model.CoinConfigEntity;
//import com.haoliang.model.TreePath;
//import org.apache.ibatis.annotations.MapKey;
//import org.apache.ibatis.annotations.Param;
//
//import java.math.BigInteger;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author Administrator
// */
//public interface CoinConfigDao extends BaseMapper<CoinConfigEntity> {
//
//    List<CoinConfigEntity> getEnableAll();
//
//    CoinConfigEntity getEnableByToken(@Param("contract") String contract);
//
//    CoinConfigEntity getByCoinId(@Param("coinId") Integer coinId);
//
//    int updateActionSeqById(@Param("id") Integer id, @Param("blockNo") BigInteger blockNo);
//
//    CoinConfigEntity getScanDataConfig();
//}
