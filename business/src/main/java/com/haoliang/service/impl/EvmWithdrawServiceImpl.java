package com.haoliang.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.enums.ReturnMessageEnum;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
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
import com.haoliang.model.vo.EvmWithdrawVO;
import com.haoliang.service.EvmWithdrawService;
import com.haoliang.service.WalletLogsService;
import com.haoliang.service.WalletsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private WalletLogsService walletLogsService;

    @Resource
    private EvmWithdrawMapper evmWithdrawMapper;


    @Override
    public JsonResult pageList(PageParam<EvmWithdraw, AppUserWithdrawCondition> pageParam) {
        if (pageParam.getSearchParam() == null) {
            pageParam.setSearchParam(new AppUserWithdrawCondition());
        }
        IPage<EvmWithdrawVO> iPage = evmWithdrawMapper.page(pageParam.getPage(), pageParam.getSearchParam());
        for (EvmWithdrawVO evmWithdrawVO : iPage.getRecords()) {
            evmWithdrawVO.setAuditStatusName(WithdrawStateEnum.getDescByState(evmWithdrawVO.getAuditStatus()));
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
        if (auditCheckDTO.getState().equals(WithdrawStateEnum.CHECK_SUCCESS.getState())) {
            //审核通过 触发提现逻辑
            if (evmWithdraw.getCoinId().equals(CoinUnitEnum.USDT.getId())) {
                //区块链提现需要等链上打币成功再
                wrapper.lambda().set(EvmWithdraw::getStatus, WithdrawStateEnum.CHECK_SUCCESS.getState());
            } else {
                //调用法币 TODO
            }
        } else {
            //取消冻结的金额
            walletsService.unFrozenAmount(evmWithdraw.getUserId(), evmWithdraw.getAmount());
        }
        this.update(wrapper);
        return JsonResult.successResult();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(EvmWithdraw evmWithdraw) {
        //TODO
        //根据币种计算手续费
        CoinUnitEnum coinUnitEnum = CoinUnitEnum.idOf(evmWithdraw.getCoinId());
        BigDecimal fee = evmWithdraw.getAmount().multiply(coinUnitEnum.getInterestRate());

        //实际提现美元
        BigDecimal actualAmountUSD = evmWithdraw.getAmount().subtract(fee);
        //换算成提现币种的金额
        BigDecimal actualAmount;
        Integer state;
        if (coinUnitEnum == CoinUnitEnum.FIAT) {
            actualAmount = actualAmountUSD.divide(EasyTradeConfig.XPF_2_USD, 7, RoundingMode.FLOOR);
            //法币金额提现
            //TODO 法币提现，走第三方提现接口
            {

            }
            state = WithdrawStateEnum.SUCCESS.getState();
            //把提现的金额往钱包表里余额减去
            walletsService.lookUpdateWallets(evmWithdraw.getUserId(), evmWithdraw.getAmount(), FlowingActionEnum.EXPENDITURE);

        } else {
            //Usdt提现 需要通过定时任务去扫描区块链打币有没有成功
            state = WithdrawStateEnum.TO_BE_CONFIRMED_BY_THE_BLOCK.getState();
            actualAmount = actualAmountUSD.divide(EasyTradeConfig.USDT_2_USD, 7, RoundingMode.FLOOR);
        }
        evmWithdraw.setFee(fee);
        evmWithdraw.setActualAmount(actualAmount);
        evmWithdraw.setStatus(state);
        this.saveOrUpdate(evmWithdraw);
    }

    @Override
    public JsonResult usdtWithdrawal(UsdtWithdrawalDTO usdtWithdrawalDTO, String token) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(token);
        Wallets wallets = walletsService.selectColumnsByUserId(userId, Wallets::getWalletAmount);

        if (usdtWithdrawalDTO.getAmount().compareTo(wallets.getWalletAmount()) > 0) {
            //提现金额不能大于钱包余额
            return JsonResult.failureResult(ReturnMessageEnum.AMOUNT_EXCEEDS_BALANCE);
        }

        CoinNetworkSourceEnum coinNetworkSourceEnum = CoinNetworkSourceEnum.nameOf(usdtWithdrawalDTO.getNetwordName());

        if(coinNetworkSourceEnum==null){
            return JsonResult.failureResult(ReturnMessageEnum.UB_SUPPORT_NETWORD);
        }

        //区块链支付需要判断提现的金额是否大于最低限制
        if (usdtWithdrawalDTO.getAmount().compareTo(CoinNetworkSourceEnum.nameOf(usdtWithdrawalDTO.getNetwordName()).getMinAmount()) < 0) {
            return JsonResult.failureResult(ReturnMessageEnum.WITHDRAW_MIN_AMOUNT);
        }

        //生成提现记录
        EvmWithdraw evmWithdraw = EvmWithdraw.builder()
                .userId(userId)
                .address(usdtWithdrawalDTO.getAddress())
                .coinName(CoinUnitEnum.USDT.getName())
                .coinUnit(usdtWithdrawalDTO.getNetwordName())
                .amount(usdtWithdrawalDTO.getAmount())
                .coinId(CoinUnitEnum.USDT.getId())
                .build();

        //添加钱包流水记录
        walletLogsService.insertWalletLogs(evmWithdraw.getAmount(), evmWithdraw.getUserId(), FlowingActionEnum.EXPENDITURE, FlowingTypeEnum.WITHDRAWAL);

        //计算费率
        BigDecimal fee = evmWithdraw.getAmount().multiply(coinNetworkSourceEnum.getFree());
        //实际提现美元
        BigDecimal actualAmountUSD = evmWithdraw.getAmount().subtract(fee);
        evmWithdraw.setFee(fee);
        evmWithdraw.setActualAmount(actualAmountUSD);
        //需要冻结提现金额
        walletsService.frozenAmount(userId, usdtWithdrawalDTO.getAmount());
        if (usdtWithdrawalDTO.getAmount().subtract(EasyTradeConfig.AMOUNT_CHECK).doubleValue() > 0) {
            //大额提现需要审核,设置提现任务状态为待审核
            evmWithdraw.setStatus(WithdrawStateEnum.UNDER_REVIEW.getState());
            evmWithdraw.setAuditStatus(WithdrawStateEnum.UNDER_REVIEW.getState());
        } else {
            //Usdt提现 需要通过定时任务去扫描区块链打币有没有成功
            evmWithdraw.setStatus(WithdrawStateEnum.CHECK_SUCCESS.getState());
            evmWithdraw.setAuditStatus(-1);
        }
        this.save(evmWithdraw);
        return JsonResult.successResult();
    }
}
