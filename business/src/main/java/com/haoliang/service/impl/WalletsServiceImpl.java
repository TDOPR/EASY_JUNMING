package com.haoliang.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.util.IdUtil;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.common.util.NumberUtil;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.*;
import com.haoliang.mapper.AppUserMapper;
import com.haoliang.mapper.EvmAddressPoolMapper;
import com.haoliang.mapper.EvmRechargeMapper;
import com.haoliang.mapper.WalletsMapper;
import com.haoliang.model.*;
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
import java.util.Map;
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
    private TreePathService treePathService;

    @Autowired
    private ProfitLogsService profitLogsService;

    @Resource
    private AppUserMapper appUserMapper;

    @Resource
    private WalletsMapper walletsMapper;

    @Resource
    private EvmUserWalletService evmUserWalletService;

    @Resource
    private EvmAddressPoolMapper evmAddressPoolMapper;

    @Resource
    private EvmRechargeMapper evmRechargeMapper;

    @Override
    public JsonResult<MyWalletsVO> getMyWallet() {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        Wallets wallets = this.selectColumnsByUserId(userId, Wallets::getWalletAmount);
        AppUsers appUsers = appUserMapper.selectOne(new LambdaQueryWrapper<AppUsers>().select(AppUsers::getMinTeamAmount, AppUsers::getTeamTotalAmount).eq(AppUsers::getId, userId));

        AppUserRebotDTO appUserRebotDTO = appUserMapper.getRobotDetailByUserId(userId);
        //????????????????????????
        MyItemVO myItemVO = MyItemVO.builder()
                .totalAmount(NumberUtil.downToInt(appUsers.getTeamTotalAmount()))
                .minAmount(NumberUtil.downToInt(appUsers.getMinTeamAmount()))
                .aiUserCount(appUserRebotDTO.getUserCount())
                .aiAmount(NumberUtil.downToInt(appUserRebotDTO.getRobotAmount()))
                .validUserCount(appUserMapper.getValidUserCountByInviteId(userId))
                .build();

        //????????????????????????
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

        //???????????????????????? ??????,??????,??????,??????
        MyWalletsVO.Quantification quantification = profitLogsService.getMyQuantification(userId);

        //??????????????????
        CoinInfoVo coinInfoVo = new CoinInfoVo();
        List<EvmUserWallet> evmUserWalletList = evmUserWalletService.list(new LambdaQueryWrapper<EvmUserWallet>()
                .select(EvmUserWallet::getAddress, EvmUserWallet::getCoinType)
                .eq(EvmUserWallet::getUserId, userId)
        );
        if (evmUserWalletList.size() == 0) {
            //??????????????????????????????????????????????????????BSC????????????????????????
            EvmUserWallet evmUserWallet = getAndSetBlockAddress(userId, CoinNetworkSourceEnum.BSC.getName());
            if (evmUserWallet != null) {
                evmUserWalletList.add(evmUserWallet);
            }
        }

        Map<String, String> blockAddressMap = evmUserWalletList.stream().collect(Collectors.toMap(EvmUserWallet::getCoinType, EvmUserWallet::getAddress));
        List<BlockAddressVo> blockAddressList = new ArrayList<>(evmUserWalletList.size());
        for (CoinNetworkSourceEnum coinNetworkSourceEnum : CoinNetworkSourceEnum.values()) {
            blockAddressList.add(new BlockAddressVo(blockAddressMap.get(coinNetworkSourceEnum.getName()), coinNetworkSourceEnum));
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
    public JsonResult recharge(WalletDTO walletDTO) {
        //????????????
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        //??????????????????????????????USD??????
        BigDecimal usdAmount = walletDTO.getAmount().multiply(EasyTradeConfig.XPF_2_USD);

        //TODO ???????????????????????????????????????
        {

        }
        //????????????????????????????????????????????????
        this.lookUpdateWallets(userId, usdAmount, FlowingActionEnum.INCOME);

        //????????????????????????
        walletLogsService.insertWalletLogs(userId, usdAmount, FlowingActionEnum.INCOME, FlowingTypeEnum.RECHARGE);

        //??????????????????
        EvmRecharge evmRecharge = EvmRecharge.builder()
                .status(RechargeStatusEnum.TO_RECORDED_SUCCESS.getStatus())
                .userId(userId)
                .coinId(CoinUnitEnum.FIAT.getId())
                .coinName(CoinUnitEnum.FIAT.getName())
                .amount(usdAmount)
                .actualAmount(usdAmount)
                .txid(IdUtil.simpleUUID())
                .build();
        evmRechargeMapper.insert(evmRecharge);
        return JsonResult.successResult();
    }

    /**
     * ????????????????????????????????????
     */
    public EvmUserWallet getAndSetBlockAddress(Integer userId, String networkName) {
        EvmAddressPool evmAddressPool = evmAddressPoolMapper.randomGetAddress(networkName);
        if (evmAddressPool != null) {
            evmAddressPoolMapper.deleteByAddress(evmAddressPool.getAddress());
            //?????????????????????????????????
            EvmUserWallet evmUserWallet = EvmUserWallet.builder()
                    .address(evmAddressPool.getAddress())
                    .coinId(evmAddressPool.getCoinId())
                    .keystore(evmAddressPool.getKeystore())
                    .valid("E")
                    .lowerAddress(evmAddressPool.getAddress().toLowerCase())
                    .coinType(evmAddressPool.getCoinType())
                    .password(evmAddressPool.getPwd())
                    .userId(userId)
                    .build();
            evmUserWalletService.save(evmUserWallet);
            return evmUserWallet;
        }
        return null;
    }


    /**
     * ????????????ID?????????????????????
     *
     * @param userId  ??????Id
     * @param columns ????????????????????????
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
        boolean flag = this.lookUpdateWallets(userId, amount, flowingActionEnum);
        if (flag) {
            //??????????????????
            walletLogsService.insertWalletLogs(userId, amount, flowingActionEnum, flowingTypeEnum);
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateWallet(String blockAddress, BigDecimal amount, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum) {
        EvmUserWallet evmUserWallet = evmUserWalletService.getOne(new LambdaQueryWrapper<EvmUserWallet>().select(EvmUserWallet::getUserId).eq(EvmUserWallet::getAddress, blockAddress));
        if (evmUserWallet == null) {
            return false;
        }
        boolean flag = this.lookUpdateWallets(evmUserWallet.getUserId(), amount, flowingActionEnum);
        if (flag) {
            //??????????????????
            walletLogsService.insertWalletLogs(evmUserWallet.getUserId(), amount, flowingActionEnum, flowingTypeEnum);
        }
        return flag;
    }


    @Override
    public MyTeamAmountDTO getMyItemAmount(List<Integer> firstUserIdList) {
        //????????????,?????????????????????
        List<TeamAmountDTO> itemAmountList = new ArrayList<>();
        List<TreePathAmountDTO> treePathAmountList;

        //???????????????
        BigDecimal itemIncome;

        Integer index = 0;
        List<Integer> userIdList;

        for (Integer id : firstUserIdList) {
            treePathAmountList = treePathService.getAllAmountByUserId(id);
            itemIncome = new BigDecimal("0");
            for (TreePathAmountDTO amountDTO : treePathAmountList) {
                //???????????????(????????????+?????????????????????)
                itemIncome = itemIncome.add(amountDTO.getTotalAmount());
            }
            userIdList = treePathAmountList.stream().map(TreePathAmountDTO::getDescendant).collect(Collectors.toList());
            itemAmountList.add(new TeamAmountDTO(itemIncome, index, userIdList));
            index++;
        }

        BigDecimal maxItemAmount = BigDecimal.ZERO;
        List<Integer> maxUserIdList = null;
        //?????????????????????1????????????????????????????????????
        if (itemAmountList.size() > 1) {
            TeamAmountDTO maxTeamAmountDTO = itemAmountList.stream().max(Comparator.comparing(x -> x.getItemIncome())).get();
            itemAmountList.remove(maxTeamAmountDTO);
            maxItemAmount = maxTeamAmountDTO.getItemIncome();
            maxUserIdList = maxTeamAmountDTO.getUserIdList();
        }

        //????????????????????????
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
    @Transactional(rollbackFor = Exception.class)
    public boolean lookUpdateWallets(Integer userId, BigDecimal amount, FlowingActionEnum flowingActionEnum) {
        int ret;
        if (flowingActionEnum.equals(FlowingActionEnum.INCOME)) {
            ret = walletsMapper.lockUpdateAddWallet(userId, amount);
        } else {
            //???
            ret = walletsMapper.lockUpdateReduceWallet(userId, amount);
        }
        return ret == 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unFrozenAmount(Integer userId, BigDecimal amount) {
        boolean flag = walletsMapper.unFrozenAmount(userId, amount) == 1;
        if (flag) {
            //??????????????????
            walletLogsService.insertWalletLogs(userId, amount, FlowingActionEnum.INCOME, FlowingTypeEnum.RECHARGE);
        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean frozenAmount(Integer userId, BigDecimal amount) {
        boolean flag = walletsMapper.frozenAmount(userId, amount) == 1;
        if (flag) {
            return walletLogsService.insertWalletLogs(userId, amount, FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.WITHDRAWAL);
        }
        return flag;
    }

    @Override
    public boolean reduceFrozenAmount(Integer userId, BigDecimal amount) {
        return walletsMapper.reduceFrozenAmount(userId, amount) == 1;
    }

    @Override
    public JsonResult getBlockAddress(String networdName) {
        CoinNetworkSourceEnum coinNetworkSourceEnum = CoinNetworkSourceEnum.nameOf(networdName);
        if (coinNetworkSourceEnum == null) {
            return JsonResult.failureResult(ReturnMessageEnum.UB_SUPPORT_NETWORD);
        }
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        EvmUserWallet evmUserWallet = evmUserWalletService.getOne(
                new LambdaQueryWrapper<EvmUserWallet>()
                        .select(EvmUserWallet::getAddress)
                        .eq(EvmUserWallet::getUserId, userId)
                        .eq(EvmUserWallet::getCoinType, networdName)
        );
        JSONObject data = new JSONObject();
        if (evmUserWallet == null) {
            //???????????????????????????
            evmUserWallet = getAndSetBlockAddress(userId, networdName);
        }
        if (evmUserWallet != null) {
            //??????????????????????????????,?????????????????????
            data.put("address", evmUserWallet.getAddress());
            return JsonResult.successResult(data);
        }
        return JsonResult.failureResult();
    }
}
