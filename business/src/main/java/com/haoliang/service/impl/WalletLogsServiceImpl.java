package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.GroupByUtils;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.common.utils.NumberUtils;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.mapper.WalletLogsMapper;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.dto.BillDetailsDTO;
import com.haoliang.model.vo.WalletLogVO;
import com.haoliang.service.WalletLogsService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 18:54
 **/
@Service
public class WalletLogsServiceImpl extends ServiceImpl<WalletLogsMapper, WalletLogs> implements WalletLogsService {

    @Resource
    private WalletLogsMapper walletLogsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insertWalletLogs(BigDecimal amount, Integer userId, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum) {
        //添加钱包流水记录
        WalletLogs walletLogs = WalletLogs.builder()
                .userId(userId)
                .amount(amount)
                .action(flowingActionEnum.getValue())
                .type(flowingTypeEnum.getValue())
                .build();
        walletLogsMapper.insert(walletLogs);
        return false;
    }

    @Override
    public JsonResult<List<WalletLogVO>> listByUserIdAndType(Integer userId, Integer type) {
        List<WalletLogs> walletLogsList = this.list(new LambdaQueryWrapper<WalletLogs>()
                .select(WalletLogs::getCreateTime, WalletLogs::getAmount, WalletLogs::getType)
                .eq(WalletLogs::getUserId, userId)
                .in(WalletLogs::getType, type)
                .orderByDesc(WalletLogs::getCreateTime));

        List<WalletLogVO> walletLogVOList = new ArrayList<>();
        for (WalletLogs walletLogs : walletLogsList) {
            walletLogVOList.add(WalletLogVO.builder()
                    .createTime(walletLogs.getCreateTime().toLocalDate().toString())
                    .amount(NumberUtils.toMoeny(walletLogs.getAmount()))
                    .name(FlowingTypeEnum.getDescByValue(walletLogs.getType()))
                    .build());
        }
        return JsonResult.successResult(walletLogVOList);
    }

    @Override
    public List<WalletLogs> getMyProxyWalletLogs(Integer userId) {
        return walletLogsMapper.getMyProxyWalletLogs(userId, Arrays.asList(FlowingTypeEnum.ALGEBRA.getValue(), FlowingTypeEnum.ROBOT.getValue(), FlowingTypeEnum.TEAM.getValue(), FlowingTypeEnum.SPECIAL.getValue()));
    }

    @Override
    public JsonResult getMybillDetails(String token, BillDetailsDTO billDetailsDTO) {
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        //根据用户Id查询流水
        List<Integer> typeList = new ArrayList<>();
        List<Integer> dynamicTypeList = Arrays.asList(FlowingTypeEnum.ALGEBRA.getValue(), FlowingTypeEnum.ROBOT.getValue(), FlowingTypeEnum.TEAM.getValue(), FlowingTypeEnum.SPECIAL.getValue());
        if (billDetailsDTO.getType() == 0) {
            //查询代理收益
            typeList.addAll(dynamicTypeList);
        } else if (billDetailsDTO.getType() != -1) {
            //其它
            typeList.add(billDetailsDTO.getType());
        }

        LambdaQueryWrapper<WalletLogs> lambdaQueryWrapper = new LambdaQueryWrapper<WalletLogs>()
                .select(WalletLogs::getCreateTime, WalletLogs::getAmount, WalletLogs::getType)
                .eq(WalletLogs::getUserId, userId)
                .orderByDesc(WalletLogs::getCreateTime);

        if (Collections.isEmpty(typeList)) {
            lambdaQueryWrapper.in(WalletLogs::getType, typeList);
        }

        List<WalletLogs> walletLogsList = this.list(lambdaQueryWrapper);
        List<WalletLogVO> walletLogVOList = new ArrayList<>();
        for (WalletLogs walletLogs : walletLogsList) {
            walletLogVOList.add(WalletLogVO.builder()
                    .type(walletLogs.getType())
                    .createTime(walletLogs.getCreateTime().toLocalDate().toString())
                    .bigDecimalAmount(walletLogs.getAmount())
                    .build());
        }

        //需要对数据分组,合并代理收益
        LinkedHashMap<String, List<WalletLogVO>> localDateListMap = GroupByUtils.collectionToMap(walletLogVOList, new GroupByUtils.GroupBy<String, WalletLogVO>() {
            @Override
            public String groupBy(WalletLogVO row) {
                return row.getCreateTime();
            }
        });

        //合并动态收益数据
        Iterator<Map.Entry<String, List<WalletLogVO>>> iterator = localDateListMap.entrySet().iterator();
        List<WalletLogVO> resultList=new ArrayList<>();
        BigDecimal dynamicAmount;
        Map.Entry<String, List<WalletLogVO>> entry;
        while (iterator.hasNext()) {
            dynamicAmount = BigDecimal.ZERO;
            entry = iterator.next();
            for (WalletLogVO walletLogVO : entry.getValue()) {
                if (dynamicTypeList.contains(walletLogVO.getType())) {
                    dynamicAmount.add(walletLogVO.getBigDecimalAmount());
                } else {
                    walletLogVO.setName(FlowingTypeEnum.getWalletDescByValue(walletLogVO.getType()));
                    walletLogVO.setAmount(NumberUtils.saveTwoDecimal(walletLogVO.getBigDecimalAmount()));
                    resultList.add(walletLogVO);
                }
            }
            if(dynamicAmount.compareTo(BigDecimal.ZERO)>0){
                resultList.add(WalletLogVO.builder()
                        .createTime(entry.getKey())
                        .amount(NumberUtils.saveTwoDecimal(dynamicAmount))
                        .name("动态收益存入")
                        .build());
            }
        }

        return JsonResult.successResult();
    }
}
