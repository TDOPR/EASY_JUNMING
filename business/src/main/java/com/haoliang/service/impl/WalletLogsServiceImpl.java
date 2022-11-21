package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.DateUtil;
import com.haoliang.common.utils.GroupByUtils;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.common.utils.NumberUtils;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.mapper.WalletLogsMapper;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.dto.BillDetailsDTO;
import com.haoliang.model.dto.DateSection;
import com.haoliang.model.vo.ProfitLogsDetailVO;
import com.haoliang.model.vo.ViewSelectVO;
import com.haoliang.model.vo.WalletLogVO;
import com.haoliang.model.vo.WalletLogsDetailVO;
import com.haoliang.service.ProfitLogsService;
import com.haoliang.service.WalletLogsService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Autowired
    private ProfitLogsService profitLogsService;

    /**
     * 动态奖励类型
     */
    private final List<Integer> dynamicTypeList = Arrays.asList(FlowingTypeEnum.ALGEBRA.getValue(), FlowingTypeEnum.ROBOT.getValue(), FlowingTypeEnum.TEAM.getValue(), FlowingTypeEnum.SPECIAL.getValue());

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
        return walletLogsMapper.insert(walletLogs) > 0;
    }

    @Override
    public JsonResult<List<WalletLogVO>> listByUserIdAndType(Integer userId, Integer type) {
        List<WalletLogs> walletLogsList = this.list(new LambdaQueryWrapper<WalletLogs>()
                .select(WalletLogs::getCreateTime, WalletLogs::getAmount, WalletLogs::getType, WalletLogs::getAction)
                .eq(WalletLogs::getUserId, userId)
                .eq(WalletLogs::getType, type)
                .orderByDesc(WalletLogs::getCreateTime));

        List<WalletLogVO> walletLogVOList = new ArrayList<>();
        for (WalletLogs walletLogs : walletLogsList) {
            walletLogVOList.add(WalletLogVO.builder()
                    .createTime(walletLogs.getCreateTime().toLocalDate().toString())
                    .amount(NumberUtils.toMoeny(walletLogs.getAmount()))
                    .name(FlowingTypeEnum.getDescByValue(walletLogs.getType()))
                    .status(walletLogs.getAction())
                    .build());
        }
        return JsonResult.successResult(walletLogVOList);
    }

    @Override
    public List<WalletLogs> getMyProxyWalletLogs(Integer userId) {
        return walletLogsMapper.getMyProxyWalletLogs(userId, dynamicTypeList);
    }

    @Override
    public JsonResult<WalletLogsDetailVO> getMybillDetails(String token, BillDetailsDTO billDetailsDTO) {
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);

        //查询钱包流水中第一笔流水的时间
        DateSection dateSection = walletLogsMapper.getDateSection(userId);
        //获取日期下拉列表
        List<ViewSelectVO> dateSelectVOList = getSelectListByUser(dateSection);


        //根据用户Id查询流水
        List<Integer> typeList = new ArrayList<>();

        if (billDetailsDTO.getType() == 0) {
            //查询代理收益
            typeList.addAll(dynamicTypeList);
        } else if (billDetailsDTO.getType() != -1) {
            //其它
            typeList.add(billDetailsDTO.getType());
        }

        LambdaQueryWrapper<WalletLogs> lambdaQueryWrapper = new LambdaQueryWrapper<WalletLogs>()
                .select(WalletLogs::getCreateTime, WalletLogs::getAmount, WalletLogs::getType, WalletLogs::getAction)
                .eq(WalletLogs::getUserId, userId)
                .orderByDesc(WalletLogs::getCreateTime);

        if (!Collections.isEmpty(typeList)) {
            lambdaQueryWrapper.in(WalletLogs::getType, typeList);
        }

        //判断是否查询所有还是根据指定月份查询
        LocalDate beginDate = null, endDate = null;
        if (!billDetailsDTO.getYearMonth().equals("-1")) {
            //分割年月
            String arr[] = billDetailsDTO.getYearMonth().split("-");
            beginDate = LocalDate.of(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), 1);
            endDate = beginDate.plusMonths(1);
            lambdaQueryWrapper.between(WalletLogs::getCreateTime, beginDate, endDate);
        }

        List<WalletLogs> walletLogsList = this.list(lambdaQueryWrapper);
        List<WalletLogVO> walletLogVOList = new ArrayList<>();
        for (WalletLogs walletLogs : walletLogsList) {
            walletLogVOList.add(WalletLogVO.builder()
                    .type(walletLogs.getType())
                    .createTime(walletLogs.getCreateTime().toLocalDate().toString())
                    .bigDecimalAmount(walletLogs.getAmount())
                    .status(walletLogs.getAction())
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
        List<WalletLogVO> resultList = new ArrayList<>();
        BigDecimal dynamicAmount;
        Map.Entry<String, List<WalletLogVO>> entry;
        //计算存入和取出金额
        BigDecimal deposit = BigDecimal.ZERO, takeOut = BigDecimal.ZERO;
        List<Integer> depositTypeList = new ArrayList<>();
        depositTypeList.addAll(Arrays.asList(FlowingTypeEnum.RECHARGE.getValue(), FlowingTypeEnum.STATIC.getValue()));
        depositTypeList.addAll(dynamicTypeList);
        while (iterator.hasNext()) {
            dynamicAmount = BigDecimal.ZERO;
            entry = iterator.next();
            for (WalletLogVO walletLogVO : entry.getValue()) {
                if (walletLogVO.getType().equals(FlowingTypeEnum.WITHDRAWAL.getValue())) {
                    takeOut = takeOut.add(walletLogVO.getBigDecimalAmount());
                } else if (depositTypeList.contains(walletLogVO.getType())) {
                    deposit = deposit.add(walletLogVO.getBigDecimalAmount());
                }

                if (dynamicTypeList.contains(walletLogVO.getType())) {
                    dynamicAmount = dynamicAmount.add(walletLogVO.getBigDecimalAmount());
                } else {
                    walletLogVO.setName(FlowingTypeEnum.getWalletDescByValue(walletLogVO.getType()));
                    walletLogVO.setAmount(NumberUtils.toUSD(walletLogVO.getBigDecimalAmount()));
                    resultList.add(walletLogVO);
                }
            }

            //需要把当天的四种动态收益合并成一条数据
            if (dynamicAmount.compareTo(BigDecimal.ZERO) > 0) {
                resultList.add(WalletLogVO.builder()
                        .createTime(entry.getKey())
                        .amount(NumberUtils.toUSD(dynamicAmount))
                        .status(1)
                        .name("动态收益存入")
                        .build());
            }
        }

        return JsonResult.successResult(WalletLogsDetailVO.builder()
                .deposit(NumberUtils.toMoeny(deposit))
                .takeOut(NumberUtils.toMoeny(takeOut))
                .walletLogList(resultList)
                .dateSectionList(dateSelectVOList)
                .build());
    }

    /**
     * 获取钱包流水月份title
     */
    private List<ViewSelectVO> getSelectListByUser(DateSection dateSection) {
        List<ViewSelectVO> viewSelectVOList = new ArrayList<>();
        viewSelectVOList.add(new ViewSelectVO("All", "-1"));
        if (dateSection!=null && dateSection.getMaxDate() != null) {
            viewSelectVOList.add(new ViewSelectVO(DateUtil.getMonthEnglish(dateSection.getMaxDate().getMonthValue()), dateSection.getMaxDate().getYear() + "-" + dateSection.getMaxDate().getMonthValue()));
            int monthNumber = DateUtil.betweenMonths(dateSection.getMinDate(), dateSection.getMaxDate());
            if (monthNumber > 11) {
                monthNumber = 11;
            }
            LocalDate localDate = dateSection.getMaxDate();
            for (int i = 1; i <= monthNumber; i++) {
                //生成最近12个月的 年-月数据
                localDate = localDate.minusMonths(1);
                viewSelectVOList.add(new ViewSelectVO(DateUtil.getMonthEnglish(localDate.getMonthValue()), localDate.getYear() + "-" + localDate.getMonthValue()));
            }
        }
        return viewSelectVOList;
    }

    @Override
    public JsonResult<ProfitLogsDetailVO> quantificationDetail(String token) {
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        List<ProfitLogs> profitLogsList = profitLogsService.list(new LambdaQueryWrapper<ProfitLogs>()
                .select(ProfitLogs::getPrincipal, ProfitLogs::getGeneratedAmount, ProfitLogs::getStatus, ProfitLogs::getCreateTime)
                .orderByDesc(ProfitLogs::getCreateTime)
                .eq(ProfitLogs::getUserId, userId));
        List<ProfitLogsDetailVO.ProfitLogsVO> profitLogsVOList = new ArrayList<>();

        BigDecimal settled = BigDecimal.ZERO, noSettled = BigDecimal.ZERO;
        for (ProfitLogs profitLogs : profitLogsList) {
            if (profitLogs.getStatus() == 1) {
                settled = settled.add(profitLogs.getGeneratedAmount());
            } else {
                noSettled = noSettled.add(profitLogs.getGeneratedAmount());
            }
            profitLogsVOList.add(ProfitLogsDetailVO.ProfitLogsVO.builder()
                    .createTime(profitLogs.getCreateTime().toLocalDate().toString())
                    .generatedAmount(NumberUtils.toTwoDecimal(profitLogs.getGeneratedAmount()))
                    .principal(NumberUtils.toTwoDecimal(profitLogs.getPrincipal()))
                    .status(profitLogs.getStatus())
                    .build());
        }

        return JsonResult.successResult(new ProfitLogsDetailVO(NumberUtils.toTwoDecimal(settled), NumberUtils.toTwoDecimal(noSettled), profitLogsVOList));
    }

    @Override
    public JsonResult<List<WalletLogVO>> proxyDetail(String token) {
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        List<WalletLogs> walletLogsList = this.list(new LambdaQueryWrapper<WalletLogs>()
                .select(WalletLogs::getCreateTime, WalletLogs::getAmount, WalletLogs::getType)
                .eq(WalletLogs::getUserId, userId)
                .in(WalletLogs::getType, dynamicTypeList)
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
}
