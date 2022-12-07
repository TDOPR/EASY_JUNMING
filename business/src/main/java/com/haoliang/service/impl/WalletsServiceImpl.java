package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.common.util.NumberUtil;
import com.haoliang.enums.CoinNetworkSourceEnum;
import com.haoliang.enums.CoinUnitEnum;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.mapper.AppUserRechargeMapper;
import com.haoliang.mapper.EvmAddressPoolMapper;
import com.haoliang.mapper.WalletsMapper;
import com.haoliang.model.AppUsers;
import com.haoliang.model.EvmUserWallet;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.Wallets;
import com.haoliang.model.dto.*;
import com.haoliang.model.vo.BlockAddressVo;
import com.haoliang.model.vo.CoinInfoVo;
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
    private EvmWithdrawService evmWithdrawService;

    @Autowired
    private TreePathService treePathService;

    @Autowired
    private ProfitLogsService profitLogsService;

    @Resource
    private AppUserRechargeMapper appUserRechargeMapper;

    @Resource
    private EvmAddressPoolMapper evmAddressPoolMapper;

    @Resource
    private AppUserMapper appUserMapper;

    @Resource
    private WalletsMapper walletsMapper;

    @Resource
    private EvmUserWalletService evmUserWalletService;


    @Override
    public JsonResult<MyWalletsVO> getMyWallet(String token) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getWalletAmount);
        AppUsers appUsers = appUserMapper.selectOne(new LambdaQueryWrapper<AppUsers>().select(AppUsers::getMinTeamAmount, AppUsers::getTeamTotalAmount).eq(AppUsers::getId, userId));

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
        //加载货币信息
        CoinInfoVo coinInfoVo = new CoinInfoVo();
        List<EvmUserWallet> evmUserWalletList = evmUserWalletService.list(new LambdaQueryWrapper<EvmUserWallet>()
                .select(EvmUserWallet::getAddress, EvmUserWallet::getCoinType)
                .eq(EvmUserWallet::getUserId, userId)
        );
        List<BlockAddressVo> blockAddressList = new ArrayList<>(evmUserWalletList.size());
        CoinNetworkSourceEnum coinNetworkSourceEnum;
        for (EvmUserWallet evmUserWallet : evmUserWalletList) {
            coinNetworkSourceEnum = CoinNetworkSourceEnum.nameOf(evmUserWallet.getCoinType());
            blockAddressList.add(new BlockAddressVo(evmUserWallet.getCoinType(), evmUserWallet.getAddress(), coinNetworkSourceEnum));
        }
        coinInfoVo.setBlockAddressList(blockAddressList);

        return JsonResult.successResult(MyWalletsVO.builder()
                .quantification(quantification)
                .proxy(proxy)
                .myItem(myItemVO)
                .usdtInterestRate(CoinUnitEnum.USDT.getInterestRate().multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue())
                .lcInterestRate(CoinUnitEnum.FIAT.getInterestRate().multiply(new BigDecimal(100)).setScale(0, RoundingMode.HALF_UP).intValue())
                .coinInfo(coinInfoVo)
                .balance(NumberUtil.toTwoDecimal(wallets.getWalletAmount()))
                .build());
    }

    @Override
    @Transactional
    public JsonResult recharge(WalletOrderDTO walletOrderDTO, String token) {
        //法币充值
//        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
//        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getId, Wallets::getBlockAddress);
//        BigDecimal usdAmount = walletOrderDTO.getAmount().divide(EasyTradeConfig.XPF_2_USD, 7, RoundingMode.FLOOR);
//        BigDecimal exchangeRate = EasyTradeConfig.XPF_2_USD;
//        Integer status = 1;
//
//        //TODO 法币充值，走第三方支付接口
//        //把充值的金额往钱包表里余额里增加
//        this.lookUpdateWallets(userId, usdAmount, FlowingActionEnum.INCOME);
//
//        //添加钱包流水记录
//        walletLogsService.insertWalletLogs(usdAmount, userId, FlowingActionEnum.INCOME, FlowingTypeEnum.RECHARGE);
//
//        //生成充值记录
//        AppUserRecharge appUserRecharge = AppUserRecharge.builder()
//                .exchangeRate(exchangeRate)
//                .status(status)
//                .address(wallets.getBlockAddress())
//                .userId(userId)
//                .txid(IdUtil.simpleUUID())
//                .coinUnit(walletOrderDTO.getCoinUnit())
//                .amount(walletOrderDTO.getAmount())
//                .usdAmount(usdAmount)
//                .build();
//
//        appUserRechargeMapper.insert(appUserRecharge);
        return JsonResult.successResult();
    }

    @Override
    @Transactional
    public JsonResult withdrawal(WalletOrderDTO walletOrderDTO, String token) {

//
//        //提现
//        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
//        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getWalletAmount);
//        CoinUnitEnum coinUnitEnum = CoinUnitEnum.valueOf(walletOrderDTO.getCoinId());
//
//        if (walletOrderDTO.getAmount().compareTo(wallets.getWalletAmount()) > 0) {
//            //提现金额不能大于钱包余额
//            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
//        }
//
//        String coinUnit = null;
//        if (coinUnitEnum.equals(CoinUnitEnum.USDT)) {
//            EvmUserWallet evmUserWallet = evmUserWalletService.getByAddress(walletOrderDTO.getAddress(), EvmUserWallet::getCoinId, EvmUserWallet::getCoinType);
//            //区块链支付需要判断提现的金额是否大于最低限制
//            if (walletOrderDTO.getAmount().compareTo(CoinNetworkSourceEnum.nameOf(evmUserWallet.getCoinType()).getMinAmount()) < 0) {
//                //TODO
//                return JsonResult.failureResult();
//            }
//            coinUnit = evmUserWallet.getCoinType();
//        }
//
//        //生成提现记录
//        EvmWithdraw evmWithdraw = EvmWithdraw.builder()
//                .userId(userId)
//                .address(walletOrderDTO.getAddress())
//                .coinName(coinUnitEnum.getName())
//                .coinUnit(coinUnit)
//                .amount(walletOrderDTO.getAmount())
//                .coinId(walletOrderDTO.getCoinId())
//                .build();
//
//
//        //添加钱包流水记录
//        walletLogsService.insertWalletLogs(evmWithdraw.getAmount(), evmWithdraw.getUserId(), FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.WITHDRAWAL);
//
//        if (walletOrderDTO.getAmount().subtract(EasyTradeConfig.AMOUNT_CHECK).doubleValue() > 0) {
//            //需要审核,设置提现任务状态为待审核
//            evmWithdraw.setStatus(WithdrawStateEnum.UNDER_REVIEW.getState());
//            evmWithdraw.setAuditStatus(WithdrawStateEnum.UNDER_REVIEW.getState());
//            evmWithdrawService.save(evmWithdraw);
//            //待审核的提现任务需要冻结提现金额
//            walletsMapper.frozenAmount(userId, walletOrderDTO.getAmount());
//        } else {
//            //小额不需要审核直接走提现逻辑
//            evmWithdraw.setAuditStatus(-1);
//            evmWithdrawService.withdraw(evmWithdraw);
//        }
        return JsonResult.successResult();
    }


    /**
     * 判断是否有区块链地址，如果没有则重地址池中取出然后设置到钱包里
     *
     * @param wallets
     * @return
     */
//    private boolean hasBlockAddress(Wallets wallets) {
//        String address = wallets.getBlockAddress();
//        if (address == null) {
//            address = evmAddressPoolMapper.getAddress();
//            if (address == null) {
//                return false;
//            }
//            evmAddressPoolMapper.deleteByAddress(address);
//            wallets.setBlockAddress(address);
//            UpdateWrapper<Wallets> wrapper = Wrappers.update();
//            wrapper.lambda()
//                    .set(Wallets::getBlockAddress, address)
//                    .eq(Wallets::getId, wallets.getId());
//            this.update(wrapper);
//        }
//        return true;
//    }


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
        EvmUserWallet evmUserWallet = evmUserWalletService.getOne(new LambdaQueryWrapper<EvmUserWallet>().select(EvmUserWallet::getUserId).eq(EvmUserWallet::getAddress, blockAddress));
        if (evmUserWallet == null) {
            return false;
        }
        this.lookUpdateWallets(evmUserWallet.getUserId(), amount, flowingActionEnum);
        //插入流水记录
        walletLogsService.insertWalletLogs(amount, evmUserWallet.getUserId(), flowingActionEnum, flowingTypeEnum);
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

    @Override
    public boolean unFrozenAmount(Integer userId, BigDecimal amount) {
        boolean flag = walletsMapper.unFrozenAmount(userId, amount) == 1;
        if (flag) {
            //添加流水记录
            return walletLogsService.insertWalletLogs(amount, userId, FlowingActionEnum.INCOME, FlowingTypeEnum.RECHARGE);
        }
        return flag;
    }

    @Override
    public boolean frozenAmount(Integer userId, BigDecimal amount) {
        return walletsMapper.frozenAmount(userId, amount) == 1;
    }

    @Override
    public boolean reduceFrozenAmount(Integer userId, BigDecimal amount) {
        return walletsMapper.reduceFrozenAmount(userId,amount)==1;
    }
}
