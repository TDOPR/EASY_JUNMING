package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.mapper.AppUserRechargeMapper;
import com.haoliang.mapper.StrategyMapper;
import com.haoliang.model.*;
import com.haoliang.model.dto.AppUserLoginDTO;
import com.haoliang.model.vo.TrusteeshipAmountVO;
import com.haoliang.service.AppUserRechargeService;
import com.haoliang.service.AppUserService;
import com.haoliang.service.StrategyService;
import com.haoliang.service.WalletsService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy> implements StrategyService {

    @Resource
    private AppUserService appUsersService;

    @Resource
    private WalletsService walletsService;

    @Resource
    private  StrategyService strategyService;

    public static String[] strategyTypeData = new String[] { "马丁格尔AI策略", "迈凯伦指数策略", "期现套利策略", "波段追踪策略", "频响定投策略", "集中频响策略",
            "逆周期跟单策略", "低阻抗追踪策略", "波段平衡策略", "阻抗均衡策略"};

    public static String[] qcData = new String[] { "BTC", "LTC", "ETH", "ETC", "XRP", "EOX",
            "ADA", "MANA", "TRX", "MKR", "BCH", "FTM", "SOL", "ATOM", "DOGE", "CEOL", "COMP", "DOT", "UNI",
            "DAI", "AAVE", "CHZ", "SHIB", "SOX", "KNC", "ZEN", "ZIL", "ANT", "SRM", "SUSHI", "UMA", "GRT",
            "1INCH", "MXC", "MASK", "XCH" };

    public static String[] bcData = new String[] { "BTC", "LTC", "ETH", "ETC", "XRP", "EOX",
            "ADA", "MANA", "TRX", "MKR", "BCH", "FTM", "SOL", "ATOM", "DOGE", "CEOL", "COMP", "DOT", "UNI",
            "DAI", "AAVE", "CHZ", "SHIB", "SOX", "KNC", "ZEN", "ZIL", "ANT", "SRM", "SUSHI", "UMA", "GRT",
            "1INCH", "MXC", "MASK", "XCH" };

    public static String[] croData = new String[] { "BTC/LTC", "ETH/ETC", "XRP/EOX", "ADA/MANA", "TRX/MKR", "BCH/FTM",
            "SOL/BTC", "LTC/ETH", "ETC/XRP", "EOX/ADA", "MANA/TRX", "MKR/BCH", "FTM/SOL", "ATOM/DOGE", "CEOL/COMP", "DOT/UNI", "DAI/AAVE", "CHZ/SHIB", "SOX/KNC",
            "ZEN/ZIL", "ANT/SRM", "SUSHI/UMA", "GRT/1INCH", "MXC/MASK", "XCH/ATOM", "DOGE/CEOL", "COMP/DOT", "UNI/DAI", "AAVE/CHZ", "SHIB/SOX", "KNC/ZEN", "ZIL/ANT",
            "SRM/SUSHI", "UMA/GRT", "1INCH/MXC", "MASK/XCH" };

    public static BigDecimal generate(int min, int max, int type, int divid) {
        double num = (Math.random() * (min - max) + max) / divid;
        BigDecimal d = new BigDecimal(num);
        d = d.setScale(type, RoundingMode.HALF_UP);
        return d;
    }

    public JsonResult<Strategy> getStrategyType(String token){

        String strategyType;
        String qc;
        String bc;
        String cro;
        BigDecimal dern;
        BigDecimal ti;
        BigDecimal eipM;
        BigDecimal eipN;
        BigDecimal t;

        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getPrincipalAmount, Wallets::getRobotLevel);
        if(wallets.getRobotLevel()==0){
            return JsonResult.failureResult("未购买机器人,不参与做单");
        }else{
            BigDecimal d = generate(0,36, 0, 1);
            strategyType = strategyTypeData[d.intValue()];
            qc = qcData[d.intValue()];
            bc = bcData[d.intValue()];
            cro = croData[d.intValue()];

            dern = generate(-10, 16, 2, 100);
            ti = generate(5, 90, 4, 100000);
            eipM = generate(2000, 8000, 0, 1);
            eipN = generate(2000, 8000, 0, 1);
            if(eipM.compareTo(eipN)>=0){
                t = eipN;
                eipN = eipM;
                eipM = t;
            }
        }

        Strategy strategy = Strategy.builder()
                .eipM(eipM)
                .eipN(eipN)
                .strategyType(strategyType)
                .createTime(LocalDateTime.now())
                .bc(bc)
                .cro(cro)
                .qc(qc)
                .dern(dern)
                .ti(ti)
                .build();
        strategyService.save(strategy);
        return JsonResult.successResult(strategy);
    }

}
