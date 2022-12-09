package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.util.DateUtil;
import com.haoliang.common.util.GroupByUtil;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.common.util.NumberUtil;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.mapper.WalletLogsMapper;
import com.haoliang.model.ProfitLogs;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.dto.BillDetailsDTO;
import com.haoliang.model.dto.DateSection;
import com.haoliang.common.model.dto.TypeDTO;
import com.haoliang.model.vo.*;
import com.haoliang.service.ProfitLogsService;
import com.haoliang.service.WalletLogsService;
import org.apache.commons.collections4.CollectionUtils;
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
    public boolean insertWalletLogs(Integer userId,BigDecimal amount, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum) {
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
                    .amount(NumberUtil.toMoeny(walletLogs.getAmount()))
                    .name(FlowingTypeEnum.getDescByValue(walletLogs.getType()))
                    .type(walletLogs.getAction())
                    .build());
        }
        return JsonResult.successResult(walletLogVOList);
    }

    @Override
    public List<WalletLogs> getMyProxyWalletLogs(Integer userId) {
        return walletLogsMapper.getMyProxyWalletLogs(userId, dynamicTypeList);
    }

    @Override
    public JsonResult<WalletLogsDetailVO> getMybillDetails( BillDetailsDTO billDetailsDTO) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());

        //查询钱包流水中第一笔流水的时间
        DateSection dateSection = walletLogsMapper.getDateSection(userId);
        //获取日期下拉列表
        List<ViewSelectVO> dateSelectVOList = billDetailsDTO.isInit() ? getSelectListByUser(dateSection) : null;
        //根据类型查询
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

        if (!CollectionUtils.isEmpty(typeList)) {
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

        Page<WalletLogs> pageData = this.page(new Page<>(billDetailsDTO.getCurrentPage(), billDetailsDTO.getPageSize()), lambdaQueryWrapper);
        List<WalletLogVO> walletLogVOList = new ArrayList<>();
        for (WalletLogs walletLogs : pageData.getRecords()) {
            walletLogVOList.add(WalletLogVO.builder()
                    .flowingType(walletLogs.getType())
                    .type(walletLogs.getAction())
                    .createTime(walletLogs.getCreateTime().toLocalDate().toString())
                    .bigDecimalAmount(walletLogs.getAmount())
                    .build());
        }

        //需要对数据分组,合并代理收益
        LinkedHashMap<String, List<WalletLogVO>> localDateListMap = GroupByUtil.collectionToMap(walletLogVOList, new GroupByUtil.GroupBy<String, WalletLogVO>() {
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
        //存入类型
        List<Integer> depositTypeList = new ArrayList<>();
        depositTypeList.addAll(Arrays.asList(FlowingTypeEnum.RECHARGE.getValue(), FlowingTypeEnum.STATIC.getValue()));
        depositTypeList.addAll(dynamicTypeList);


        while (iterator.hasNext()) {
            dynamicAmount = BigDecimal.ZERO;
            entry = iterator.next();
            for (WalletLogVO walletLogVO : entry.getValue()) {
                if (walletLogVO.getFlowingType().equals(FlowingTypeEnum.WITHDRAWAL.getValue())) {
                    //计算存入金额
                    takeOut = takeOut.add(walletLogVO.getBigDecimalAmount());
                } else if (depositTypeList.contains(walletLogVO.getFlowingType())) {
                    //计算取出金额
                    deposit = deposit.add(walletLogVO.getBigDecimalAmount());
                }

                if (dynamicTypeList.contains(walletLogVO.getFlowingType())) {
                    dynamicAmount = dynamicAmount.add(walletLogVO.getBigDecimalAmount());
                } else {
                    walletLogVO.setName(FlowingTypeEnum.getWalletDescByValue(walletLogVO.getFlowingType()));
                    walletLogVO.setAmount(NumberUtil.toUSD(walletLogVO.getBigDecimalAmount()));
                    resultList.add(walletLogVO);
                }
            }

            //需要把当天的四种动态收益合并成一条数据
            if (dynamicAmount.compareTo(BigDecimal.ZERO) > 0) {
                resultList.add(WalletLogVO.builder()
                        .createTime(entry.getKey())
                        .amount(NumberUtil.toUSD(dynamicAmount))
                        .type(1)
                        .name("动态收益存入")
                        .build());

            }
        }
        List<ViewSelectVO> viewTypeList = billDetailsDTO.isInit() ? WalletLogsDetailVO.buildTypeList() : null;
        return JsonResult.successResult(WalletLogsDetailVO.builder()
                .deposit(NumberUtil.toMoeny(deposit))
                .takeOut(NumberUtil.toMoeny(takeOut))
                .tableList(resultList)
                .typeList(viewTypeList)
                .dateSectionList(dateSelectVOList)
                .totalPage((int) pageData.getPages())
                .build());
    }

    /**
     * 获取钱包流水月份title
     */
    private List<ViewSelectVO> getSelectListByUser(DateSection dateSection) {
        List<ViewSelectVO> viewSelectVOList = new ArrayList<>();
        viewSelectVOList.add(new ViewSelectVO("All", "-1"));
        if (dateSection != null && dateSection.getMaxDate() != null) {
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
    public JsonResult<ProfitLogsDetailVO> quantificationDetail(TypeDTO typeDTO) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());

        LambdaQueryWrapper<ProfitLogs> lambdaQueryWrapper = new LambdaQueryWrapper<ProfitLogs>()
                .select(ProfitLogs::getPrincipal, ProfitLogs::getGeneratedAmount, ProfitLogs::getStatus, ProfitLogs::getCreateDate)
                .orderByDesc(ProfitLogs::getCreateDate)
                .eq(ProfitLogs::getUserId, userId);

        //是否根据交割类型查询数据
        if (typeDTO.getType() > -1) {
            lambdaQueryWrapper.eq(ProfitLogs::getStatus, typeDTO.getType());
        }

        Page<ProfitLogs> page = profitLogsService.page(new Page<>(typeDTO.getCurrentPage(), typeDTO.getPageSize()), lambdaQueryWrapper);
        List<ProfitLogsDetailVO.ProfitLogsVO> profitLogsVOList = new ArrayList<>();

        BigDecimal settled = profitLogsService.getTotalAmountByUserIdAndType(userId, 1);
        BigDecimal noSettled = profitLogsService.getTotalAmountByUserIdAndType(userId, 0);
        for (ProfitLogs profitLogs : page.getRecords()) {
            profitLogsVOList.add(ProfitLogsDetailVO.ProfitLogsVO.builder()
                    .createTime(profitLogs.getCreateDate().toString())
                    .generatedAmount(NumberUtil.toTwoDecimal(profitLogs.getGeneratedAmount()))
                    .principal(NumberUtil.toTwoDecimal(profitLogs.getPrincipal()))
                    .type(profitLogs.getStatus())
                    .build());
        }

        List<ViewSelectVO> typeList = null;
        if (typeDTO.isInit()) {
            //首页加载需要返回类型列表
            typeList = new ArrayList<>();
            typeList.add(new ViewSelectVO("全部", "-1"));
            typeList.add(new ViewSelectVO("已交割", "1"));
            typeList.add(new ViewSelectVO("未交割", "0"));
        }

        return JsonResult.successResult(ProfitLogsDetailVO.builder()
                .totalPage((int) page.getPages())
                .list(profitLogsVOList)
                .typeList(typeList)
                .settled(NumberUtil.toTwoDecimal(settled))
                .noSettled(NumberUtil.toTwoDecimal(noSettled))
                .totalAmount(NumberUtil.toTwoDecimal(settled.add(noSettled)))
                .build());
    }

    @Override
    public JsonResult<ProxyWalletLogsDetailVO> proxyDetail(TypeDTO typeDTO) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        LambdaQueryWrapper<WalletLogs> lambdaQueryWrapper = new LambdaQueryWrapper<WalletLogs>()
                .select(WalletLogs::getCreateTime, WalletLogs::getAmount, WalletLogs::getType)
                .eq(WalletLogs::getUserId, userId)
                .orderByDesc(WalletLogs::getCreateTime);

        if (typeDTO.getType() == -1) {
            //查询所有动态奖励
            lambdaQueryWrapper.in(WalletLogs::getType, dynamicTypeList);
        } else {
            //根据类型查询
            lambdaQueryWrapper.eq(WalletLogs::getType, typeDTO.getType());
        }

        Page<WalletLogs> page = this.page(new Page<>(typeDTO.getCurrentPage(), typeDTO.getPageSize()), lambdaQueryWrapper);
        List<WalletLogVO> walletLogVOList = new ArrayList<>();

        String typeName;
        for (WalletLogs walletLogs : page.getRecords()) {
            typeName = FlowingTypeEnum.getDescByValue(walletLogs.getType());
            walletLogVOList.add(WalletLogVO.builder()
                    .createTime(walletLogs.getCreateTime().toLocalDate().toString())
                    .amount(NumberUtil.toMoeny(walletLogs.getAmount()))
                    .name(typeName)
                    .build());
        }

        ProxyWalletLogsDetailVO proxyWalletLogsDetailVO = new ProxyWalletLogsDetailVO();
        proxyWalletLogsDetailVO.setList(walletLogVOList);
        proxyWalletLogsDetailVO.setTotalPage((int) page.getPages());

        if (typeDTO.isInit()) {
            //首页加载需要返回类型列表
            List<ViewSelectVO> typeList = new ArrayList<>();
            typeList.add(new ViewSelectVO("全部", "-1"));
            FlowingTypeEnum flowingTypeEnum;
            for (Integer type : dynamicTypeList) {
                flowingTypeEnum = FlowingTypeEnum.valueOf(type);
                typeList.add(new ViewSelectVO(flowingTypeEnum.getDesc(), flowingTypeEnum.getValue().toString()));
            }
            proxyWalletLogsDetailVO.setTypeList(typeList);
        }

        return JsonResult.successResult(proxyWalletLogsDetailVO);
    }


}
