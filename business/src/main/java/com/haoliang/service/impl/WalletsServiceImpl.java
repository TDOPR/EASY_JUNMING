package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.util.IdUtil;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.common.util.NumberUtil;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.CoinUnitEnum;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.WithdrawStateEnum;
import com.haoliang.mapper.AddressPoolMapper;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.mapper.AppUserRechargeMapper;
import com.haoliang.mapper.WalletsMapper;
import com.haoliang.model.*;
import com.haoliang.model.dto.*;
import com.haoliang.model.vo.MyItemVO;
import com.haoliang.model.vo.MyWalletsVO;
import com.haoliang.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 12:20
 **/
@Slf4j
@Service
public class WalletsServiceImpl extends ServiceImpl<WalletsMapper, Wallets> implements WalletsService {

    @Autowired
    private WalletLogsService walletLogsService;

    @Autowired
    private AppUserWithdrawService appUserWithdrawService;

    @Autowired
    private TreePathService treePathService;

    @Autowired
    private ProfitLogsService profitLogsService;


    @Resource
    private AppUserRechargeMapper appUserRechargeMapper;

    @Resource
    private AddressPoolMapper addressPoolMapper;

    @Resource
    private AppUserMapper appUserMapper;

    @Resource
    private WalletsMapper walletsMapper;


    @Override
    public JsonResult<MyWalletsVO> getMyWallet(String token) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getWalletAmount, Wallets::getBlockAddress);
        AppUsers appUsers = appUserMapper.selectOne(new LambdaQueryWrapper<AppUsers>().select(AppUsers::getMinTeamAmount,AppUsers::getTeamTotalAmount).eq(AppUsers::getId,userId));

        AppUserRebotDTO appUserRebotDTO = appUserMapper.getRobotDetailByUserId(userId);
        //获取我的团队信息
        MyItemVO myItemVO = MyItemVO.builder()
                .totalAmount(NumberUtil.downToInt(appUsers.getTeamTotalAmount()))
                .minAmount(NumberUtil.downToInt(appUsers.getMinTeamAmount()))
                .aiUserCount(appUserRebotDTO.getUserCount())
                .aiAmount(NumberUtil.downToInt(appUserRebotDTO.getRobotAmount()))
                .validUserCount(appUserMapper.getValidUserCountByInviteId(userId))
                .build();

        //获取我的代理收益
        BigDecimal algebra = BigDecimal.ZERO, rebot = BigDecimal.ZERO, team = BigDecimal.ZERO, special = BigDecimal.ZERO;
        List<WalletLogs> walletLogsList = walletLogsService.getMyProxyWalletLogs(userId);
        for (WalletLogs walletLogs : walletLogsList) {
            if (walletLogs.getType().equals(FlowingTypeEnum.ALGEBRA.getValue())) {
                algebra = walletLogs.getAmount();
            } else if (walletLogs.getType().equals(FlowingTypeEnum.ROBOT.getValue())) {
                rebot = walletLogs.getAmount();
            } else if (walletLogs.getType().equals(FlowingTypeEnum.TEAM.getValue())) {
                team = walletLogs.getAmount();
            } else {
                special = walletLogs.getAmount();
            }
        }

        MyWalletsVO.Proxy proxy = MyWalletsVO.Proxy.builder()
                .algebra(NumberUtil.toTwoDecimal(algebra))
                .rebot(NumberUtil.toTwoDecimal(rebot))
                .team(NumberUtil.toTwoDecimal(team))
                .special(NumberUtil.toTwoDecimal(special))
                .totalAmount(NumberUtil.toTwoDecimal(algebra.add(rebot).add(team).add(special)))
                .build();

        //获取我的量化收益 昨天,上周,上月,累计
        MyWalletsVO.Quantification quantification = profitLogsService.getMyQuantification(userId);

        return JsonResult.successResult(MyWalletsVO.builder()
                .quantification(quantification)
                .proxy(proxy)
                .myItem(myItemVO)
                .usdtInterestRate(CoinUnitEnum.USDT.getInterestRate().multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue())
                .lcInterestRate(CoinUnitEnum.LEGAL_CURRENCY.getInterestRate().multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue())
                .blockAddress(wallets.getBlockAddress())
                .balance(NumberUtil.toTwoDecimal(wallets.getWalletAmount()))
                .build());
    }

    @Override
    @Transactional
    public JsonResult recharge(WalletOrderDTO walletOrderDTO, String token) {
        //法币充值
        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getBlockAddress);
        BigDecimal usdAmount = walletOrderDTO.getAmount().divide(EasyTradeConfig.XPF_2_USD, 7, RoundingMode.FLOOR);
        BigDecimal exchangeRate = EasyTradeConfig.XPF_2_USD;
        Integer status = 1;

        //TODO 法币充值，走第三方支付接口
        //把充值的金额往钱包表里余额里增加
        this.lookUpdateWallets(userId, usdAmount, FlowingActionEnum.INCOME);

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(usdAmount, userId, FlowingActionEnum.INCOME, FlowingTypeEnum.RECHARGE);

        //生成充值记录
        AppUserRecharge appUserRecharge = AppUserRecharge.builder()
                .exchangeRate(exchangeRate)
                .status(status)
                .address(wallets.getBlockAddress())
                .userId(userId)
                .txid(IdUtil.simpleUUID())
                .coinUnit(walletOrderDTO.getCoinUnit())
                .amount(walletOrderDTO.getAmount())
                .usdAmount(usdAmount)
                .build();

        appUserRechargeMapper.insert(appUserRecharge);
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult withdrawal(WalletOrderDTO walletOrderDTO, String token) {
        //提现
        Integer userId = JwtTokenUtil.getUserIdFromToken(token);

        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getBlockAddress);

        //生成提现记录
        AppUserWithdraw appUserWithdraw = AppUserWithdraw.builder()
                .userId(userId)
                .txid(IdUtil.simpleUUID())
                .address(walletOrderDTO.getBlockAddress())
                .coinUnit(walletOrderDTO.getCoinUnit())
                .amount(walletOrderDTO.getAmount())
                .build();

        if (walletOrderDTO.getAmount().subtract(EasyTradeConfig.AMOUNT_CHECK).doubleValue() > 0) {
            //需要审核,设置提现任务状态为待审核
            appUserWithdraw.setStatus(WithdrawStateEnum.UNDER_REVIEW.getState());
            appUserWithdraw.setAuditStatus(WithdrawStateEnum.UNDER_REVIEW.getState());
            appUserWithdrawService.save(appUserWithdraw);
        } else {
            //小额不需要审核直接走提现逻辑
            appUserWithdrawService.withdraw(appUserWithdraw, wallets);
        }
        return JsonResult.successResult();
    }


    /**
     * 判断是否有区块链地址，如果没有则重地址池中取出然后设置到钱包里
     *
     * @param wallets
     * @return
     */
    private boolean hasBlockAddress(Wallets wallets) {
        String address = wallets.getBlockAddress();
        if (address == null) {
            address = addressPoolMapper.getAddress();
            if (address == null) {
                return false;
            }
            addressPoolMapper.deleteByAddress(address);
            wallets.setBlockAddress(address);
            UpdateWrapper<Wallets> wrapper = Wrappers.update();
            wrapper.lambda()
                    .set(Wallets::getBlockAddress, address)
                    .eq(Wallets::getId, wallets.getId());
            this.update(wrapper);
        }
        return true;
    }


    /**
     * 根据用户ID查询指定列数据
     *
     * @param userId  用户Id
     * @param columns 需要查询的指定列
     * @return
     */
    @Override
    public Wallets selectColumnsByUserId(Integer userId, SFunction<Wallets, ?>... columns) {
        Wallets wallets = this.getOne(new LambdaQueryWrapper<Wallets>().select(columns).eq(Wallets::getUserId, userId));
        return wallets;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWallet(BigDecimal amount, Integer userId, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum) {
        this.lookUpdateWallets(userId, amount, flowingActionEnum);
        //插入流水记录
        walletLogsService.insertWalletLogs(amount, userId, flowingActionEnum, flowingTypeEnum);
        return true;
    }

    @Override
    public boolean updateWallet(BigDecimal amount, String blockAddress, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum) {
        Wallets wallets = this.getOne(new LambdaQueryWrapper<Wallets>().select(Wallets::getId, Wallets::getUserId, Wallets::getWalletAmount).eq(Wallets::getBlockAddress, blockAddress));
        if (wallets == null) {
            return false;
        }
        UpdateWrapper<Wallets> walletsUpdateWrapper = Wrappers.update();
        BigDecimal result;
        if (flowingActionEnum == FlowingActionEnum.INCOME) {
            result = wallets.getWalletAmount().add(amount);
        } else {
            result = wallets.getWalletAmount().subtract(amount);
        }
        walletsUpdateWrapper.lambda()
                .set(Wallets::getWalletAmount, result)
                .eq(Wallets::getId, wallets.getId());
        this.update(walletsUpdateWrapper);
        //插入流水记录
        walletLogsService.insertWalletLogs(amount, wallets.getUserId(), flowingActionEnum, flowingTypeEnum);
        return true;
    }


    @Override
    public MyTeamAmountDTO getMyItemAmount(List<Integer> firstUserIdList) {
        //团队收益,需要去除大团队
        List<TeamAmountDTO> itemAmountList = new ArrayList<>();
        List<TreePathAmountDTO> treePathAmountList;

        //团队总业绩
        BigDecimal itemIncome;

        Integer index = 0;
        List<Integer> userIdList;

        for (Integer id : firstUserIdList) {
            treePathAmountList = treePathService.getAllAmountByUserId(id);
            itemIncome = new BigDecimal("0");
            for (TreePathAmountDTO amountDTO : treePathAmountList) {
                //计算总业绩(托管金额+购买机器人金额)
                itemIncome = itemIncome.add(amountDTO.getTotalAmount());
            }
            userIdList = treePathAmountList.stream().map(TreePathAmountDTO::getDescendant).collect(Collectors.toList());
            itemAmountList.add(new TeamAmountDTO(itemIncome, index, userIdList));
            index++;
        }

        BigDecimal maxItemAmount = BigDecimal.ZERO;
        List<Integer> maxUserIdList = null;
        //当团队数量大于1的时候去除业绩最高的团队
        if (itemAmountList.size() > 1) {
            TeamAmountDTO maxTeamAmountDTO = itemAmountList.stream().max(Comparator.comparing(x -> x.getItemIncome())).get();
            itemAmountList.remove(maxTeamAmountDTO);
            maxItemAmount = maxTeamAmountDTO.getItemIncome();
            maxUserIdList = maxTeamAmountDTO.getUserIdList();
        }

        //计算小团队总业绩
        BigDecimal minSum = itemAmountList.stream().map(TeamAmountDTO::getItemIncome).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Integer> minUserIdList = new ArrayList<>();
        for (TeamAmountDTO teamAmountDTO : itemAmountList) {
            minUserIdList.addAll(teamAmountDTO.getUserIdList());
        }

        return MyTeamAmountDTO.builder()
                .minItemAmount(minSum)
                .maxItemAmount(maxItemAmount)
                .minItemUserIdList(minUserIdList)
                .maxItemUserIdList(maxUserIdList)
                .build();
    }

    @Override
    public MyTeamAmountDTO getMyItemAmountByUserId(Integer userId) {
        List<AppUsers> appUsersList = appUserMapper.selectList(new LambdaQueryWrapper<AppUsers>().select(AppUsers::getId).eq(AppUsers::getInviteId, userId));
        return getMyItemAmount(appUsersList.stream().map(AppUsers::getId).collect(Collectors.toList()));
    }

    @Override
    public BigDecimal getPlatformTotalLockAmount() {
        return walletsMapper.getPlatformTotalLockAmount();
    }


    @Override
    public List<UserWalletsDTO> selectUserWalletsDTOListByUserLevelGtAndPrincipalAmountGe(int level, BigDecimal proxyMinMoney) {
        return walletsMapper.selectUserWalletsDTOListByUserLevelGtAndPrincipalAmountGe(level, proxyMinMoney);
    }

    @Override
    public boolean lookUpdateWallets(Integer userId, BigDecimal amount, FlowingActionEnum flowingActionEnum) {
        int ret;
        if (flowingActionEnum.equals(FlowingActionEnum.INCOME)) {
            ret = walletsMapper.lockUpdateAddWallet(userId, amount);
        } else {
            //减
            ret = walletsMapper.lockUpdateReduceWallet(userId, amount);
        }
        return ret == 1;
    }
}
