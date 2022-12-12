package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.util.NumberUtil;
import com.haoliang.common.util.RandomUtil;
import com.haoliang.enums.StrategyEnum;
import com.haoliang.mapper.StrategyMapper;
import com.haoliang.model.Strategy;
import com.haoliang.service.StrategyService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
@CacheConfig(cacheNames = CacheKeyPrefixConstants.ROBOT_STRATEGY)
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy> implements StrategyService {

    /**
     * 策略类型Id
     */
    public static List<Integer> strategyIdList = StrategyEnum.getTypeList();

    public static String[] qcData = new String[]{"BTC", "LTC", "ETH", "ETC", "XRP", "EOX",
            "ADA", "MANA", "TRX", "MKR", "BCH", "FTM", "SOL", "ATOM", "DOGE", "CEOL", "COMP", "DOT", "UNI",
            "DAI", "AAVE", "CHZ", "SHIB", "SOX", "KNC", "ZEN", "ZIL", "ANT", "SRM", "SUSHI", "UMA", "GRT",
            "1INCH", "MXC", "MASK", "XCH"};

    public static String[] bcData = new String[]{"BTC", "LTC", "ETH", "ETC", "XRP", "EOX",
            "ADA", "MANA", "TRX", "MKR", "BCH", "FTM", "SOL", "ATOM", "DOGE", "CEOL", "COMP", "DOT", "UNI",
            "DAI", "AAVE", "CHZ", "SHIB", "SOX", "KNC", "ZEN", "ZIL", "ANT", "SRM", "SUSHI", "UMA", "GRT",
            "1INCH", "MXC", "MASK", "XCH"};

    public static String[] croData = new String[]{"BTC/LTC", "ETH/ETC", "XRP/EOX", "ADA/MANA", "TRX/MKR", "BCH/FTM",
            "SOL/BTC", "LTC/ETH", "ETC/XRP", "EOX/ADA", "MANA/TRX", "MKR/BCH", "FTM/SOL", "ATOM/DOGE", "CEOL/COMP", "DOT/UNI", "DAI/AAVE", "CHZ/SHIB", "SOX/KNC",
            "ZEN/ZIL", "ANT/SRM", "SUSHI/UMA", "GRT/1INCH", "MXC/MASK", "XCH/ATOM", "DOGE/CEOL", "COMP/DOT", "UNI/DAI", "AAVE/CHZ", "SHIB/SOX", "KNC/ZEN", "ZIL/ANT",
            "SRM/SUSHI", "UMA/GRT", "1INCH/MXC", "MASK/XCH"};


    @Override
    @CacheEvict(allEntries = true)
    public List<Strategy> insertStrategy(LocalDate localDate) {

        List<Integer> strategyTypeDataList = new ArrayList<>(strategyIdList);
        Integer length = strategyTypeDataList.size();
        List<Strategy> strategyList = new ArrayList<>();

        Integer strategyType;
        String qc, bc, cro;
        BigDecimal dern, ti, eipM, eipN, t;

        List<Integer> existsQcList = new ArrayList<>();

        //qc,bc,cro数据使用的随机下标
        Integer index;
        //生成5种不同的策略
        for (int i = 0; i < 5; i++) {
            //取出一条策略并重集合中移除该数据,保证数据不重复
            strategyType = strategyTypeDataList.remove(RandomUtil.randomInt(length));
            length--;
            index = RandomUtil.randomInt(qcData.length);
            while (existsQcList.contains(index)) {
                //保证不使用重复下标的数据
                index = RandomUtil.randomInt(qcData.length);
            }
            existsQcList.add(index);
            qc = qcData[index];
            bc = bcData[index];
            cro = croData[index];

            dern = RandomUtil.generate(-10, 16, 2, 100);
            ti = RandomUtil.generate(5, 90, 4, 100000);
            eipM = RandomUtil.generate(2000, 8000, 0, 1);
            eipN = RandomUtil.generate(2000, 8000, 0, 1);
            if (eipM.compareTo(eipN) >= 0) {
                t = eipN;
                eipN = eipM;
                eipM = t;
            }

            strategyList.add(
                    Strategy.builder()
                            .eipM(NumberUtil.toTwoDecimal(eipM))
                            .eipN(NumberUtil.toTwoDecimal(eipN))
                            .strategyType(strategyType)
                            .createDate(LocalDate.now())
                            .sortIndex(i + 1)
                            .bc(bc)
                            .cro(cro)
                            .qc(qc)
                            .dern(dern + "%")
                            .ti("ln" + ti)
                            .eip("Φ:" + eipN + "->" + eipM)
                            .build()
            );
        }
        this.saveBatch(strategyList);
        return strategyList;
    }

    @Override
    @Cacheable(key = "#robotLevel")
    public List<Strategy> getStrategyListByRobotLevel(Integer robotLevel) {
        LocalDate now = LocalDate.now();
        List<Strategy> strategyList = this.list(new LambdaQueryWrapper<Strategy>()
                .eq(Strategy::getCreateDate, now)
                .le(Strategy::getSortIndex, robotLevel)
        );
        if (CollectionUtils.isEmpty(strategyList)) {
            strategyList = insertStrategy(now);
            //根据等级获取对应条数记录
            strategyList = new ArrayList<>(strategyList.subList(0, robotLevel));
        }
        return strategyList;
    }

}