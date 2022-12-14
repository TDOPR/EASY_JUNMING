package com.haoliang.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.*;
import com.haoliang.mapper.EvmWithdrawMapper;
import com.haoliang.model.EvmWithdraw;
import com.haoliang.model.Wallets;
import com.haoliang.model.condition.AppUserWithdrawCondition;
import com.haoliang.model.dto.AuditCheckDTO;
import com.haoliang.model.dto.UsdtWithdrawalDTO;
import com.haoliang.model.dto.WalletDTO;
import com.haoliang.model.vo.EvmWithdrawVO;
import com.haoliang.service.EvmWithdrawService;
import com.haoliang.service.WalletsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class EvmWithdrawServiceImpl extends ServiceImpl<EvmWithdrawMapper, EvmWithdraw> implements EvmWithdrawService {

    @Resource
    private WalletsService walletsService;

    @Resource
    private EvmWithdrawMapper evmWithdrawMapper;


    @Override
    public JsonResult pageList(PageParam<EvmWithdraw, AppUserWithdrawCondition> pageParam) {
        if (pageParam.getSearchParam() == null) {
            pageParam.setSearchParam(new AppUserWithdrawCondition());
        }
        IPage<EvmWithdrawVO> iPage = evmWithdrawMapper.page(pageParam.getPage(), pageParam.getSearchParam());
        for (EvmWithdrawVO evmWithdrawVO : iPage.getRecords()) {
            evmWithdrawVO.setAuditStatusName(WithdrawStatusEnum.getDescByStatus(evmWithdrawVO.getAuditStatus()));
        }
        return JsonResult.successResult(new PageVO<>(iPage.getTotal(), iPage.getPages(), iPage.getRecords()));
    }

    @Override
    public JsonResult check(AuditCheckDTO auditCheckDTO) {
        UpdateWrapper<EvmWithdraw> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(EvmWithdraw::getAuditStatus, auditCheckDTO.getState())
                .set(EvmWithdraw::getAuditTime, LocalDateTime.now())
                .eq(EvmWithdraw::getId, auditCheckDTO.getId());
        EvmWithdraw evmWithdraw = this.getById(auditCheckDTO.getId());
        if (auditCheckDTO.getState().equals(WithdrawStatusEnum.CHECK_SUCCESS.getStatus())) {
            //???????????? ??????????????????
            if (evmWithdraw.getCoinId().equals(CoinUnitEnum.USDT.getId())) {
                //?????????????????????????????????????????????
                wrapper.lambda().set(EvmWithdraw::getStatus, WithdrawStatusEnum.CHECK_SUCCESS.getStatus());
            } else {
                boolean flag = this.playFiat(evmWithdraw);
                if (flag) {
                    //?????????????????????
                    walletsService.reduceFrozenAmount(evmWithdraw.getUserId(), evmWithdraw.getAmount());
                } else {
                    //????????????????????????????????????????????????????????????
                    walletsService.unFrozenAmount(evmWithdraw.getUserId(), evmWithdraw.getAmount());
                }
            }
        } else {
            //?????????????????????
            walletsService.unFrozenAmount(evmWithdraw.getUserId(), evmWithdraw.getAmount());
        }
        this.update(wrapper);
        return JsonResult.successResult();
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResult usdtWithdrawal(UsdtWithdrawalDTO usdtWithdrawalDTO) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getWalletAmount);

        if (usdtWithdrawalDTO.getAmount().compareTo(wallets.getWalletAmount()) > 0) {
            //????????????????????????????????????
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        CoinNetworkSourceEnum coinNetworkSourceEnum = CoinNetworkSourceEnum.nameOf(usdtWithdrawalDTO.getNetwordName());

        if (coinNetworkSourceEnum == null) {
            return JsonResult.failureResult(ReturnMessageEnum.UB_SUPPORT_NETWORD);
        }

        //??????????????????????????????????????????????????????????????????
        if (usdtWithdrawalDTO.getAmount().compareTo(coinNetworkSourceEnum.getMinAmount()) < 0) {
            return JsonResult.failureResult(ReturnMessageEnum.WITHDRAW_MIN_AMOUNT.setAndToString(coinNetworkSourceEnum.getMinAmount()));
        }

        //??????????????????
        EvmWithdraw evmWithdraw = EvmWithdraw.builder()
                .userId(userId)
                .address(usdtWithdrawalDTO.getAddress())
                .coinName(CoinUnitEnum.USDT.getName())
                .coinUnit(usdtWithdrawalDTO.getNetwordName())
                .amount(usdtWithdrawalDTO.getAmount())
                .coinId(CoinUnitEnum.USDT.getId())
                .build();


        //????????????
        BigDecimal fee = evmWithdraw.getAmount().multiply(coinNetworkSourceEnum.getFree());
        //??????????????????
        BigDecimal actualAmountUSD = evmWithdraw.getAmount().subtract(fee);
        evmWithdraw.setFee(fee);
        evmWithdraw.setActualAmount(actualAmountUSD);
        //????????????????????????
        walletsService.frozenAmount(userId, usdtWithdrawalDTO.getAmount());
        if (usdtWithdrawalDTO.getAmount().subtract(EasyTradeConfig.AMOUNT_CHECK).doubleValue() > 0) {
            //????????????????????????,????????????????????????????????????
            evmWithdraw.setStatus(WithdrawStatusEnum.UNDER_REVIEW.getStatus());
            evmWithdraw.setAuditStatus(WithdrawStatusEnum.UNDER_REVIEW.getStatus());
        } else {
            //Usdt?????? ???????????????????????????????????????????????????????????????
            evmWithdraw.setStatus(WithdrawStatusEnum.CHECK_SUCCESS.getStatus());
        }
        this.save(evmWithdraw);
        return JsonResult.successResult();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JsonResult fiatWithdrawal(WalletDTO walletDTO) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getWalletAmount);

        if (walletDTO.getAmount().compareTo(wallets.getWalletAmount()) > 0) {
            //????????????????????????????????????
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        //??????????????????
        EvmWithdraw evmWithdraw = EvmWithdraw.builder()
                .userId(userId)
                .coinName(CoinUnitEnum.FIAT.getName())
                .amount(walletDTO.getAmount())
                .coinId(CoinUnitEnum.FIAT.getId())
                .build();

        //????????????
        BigDecimal fee = evmWithdraw.getAmount().multiply(CoinUnitEnum.FIAT.getInterestRate());
        //??????????????????
        BigDecimal actualAmountUSD = evmWithdraw.getAmount().subtract(fee);
        evmWithdraw.setFee(fee);
        evmWithdraw.setActualAmount(actualAmountUSD);
        if (evmWithdraw.getAmount().subtract(EasyTradeConfig.AMOUNT_CHECK).doubleValue() > 0) {
            //????????????????????????
            walletsService.frozenAmount(userId, evmWithdraw.getAmount());
            evmWithdraw.setStatus(WithdrawStatusEnum.UNDER_REVIEW.getStatus());
            evmWithdraw.setAuditStatus(WithdrawStatusEnum.UNDER_REVIEW.getStatus());
        } else {
            //??????????????????????????????
            evmWithdraw.setStatus(WithdrawStatusEnum.TO_AMOUNT_SUCCESS.getStatus());
            boolean flag = this.playFiat(evmWithdraw);
            if (!flag) {
                return JsonResult.failureResult();
            }
            //??????????????????
            walletsService.updateWallet(evmWithdraw.getAmount(), userId, FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.WITHDRAWAL);
        }
        this.save(evmWithdraw);
        return JsonResult.successResult();
    }

    /**
     * ??????????????????API
     *
     * @param evmWithdraw ????????????
     * @return
     */
    private boolean playFiat(EvmWithdraw evmWithdraw) {
        //???????????????????????? TODO
        {

        }
        return true;
    }
}
