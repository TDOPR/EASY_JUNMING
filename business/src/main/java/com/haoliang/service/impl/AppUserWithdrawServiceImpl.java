package com.haoliang.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.PageParam;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.constant.EasyTradeConfig;
import com.haoliang.enums.CoinUnitEnum;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.enums.WithdrawStateEnum;
import com.haoliang.mapper.AppUserWithdrawMapper;
import com.haoliang.mapper.WalletLogsMapper;
import com.haoliang.mapper.WalletMapper;
import com.haoliang.model.AppUserWithdraw;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.Wallets;
import com.haoliang.model.condition.AppUserWithdrawCondition;
import com.haoliang.model.dto.AuditCheckDTO;
import com.haoliang.model.vo.AppUserWithdrawVO;
import com.haoliang.service.AppUserWithdrawService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/1 17:59
 **/
@Service
public class AppUserWithdrawServiceImpl extends ServiceImpl<AppUserWithdrawMapper, AppUserWithdraw> implements AppUserWithdrawService {

    @Resource
    private WalletMapper walletMapper;

    @Resource
    private WalletLogsMapper walletLogsMapper;

    @Resource
    private AppUserWithdrawMapper appUserWithdrawMapper;

    @Override
    public JsonResult pageList(PageParam<AppUserWithdraw, AppUserWithdrawCondition> pageParam) {
        IPage<AppUserWithdrawVO> iPage = appUserWithdrawMapper.page(pageParam.getPage(), pageParam.getSearchParam());
        for (AppUserWithdrawVO appUserWithdrawVO : iPage.getRecords()) {
            appUserWithdrawVO.setAuditStatusName(WithdrawStateEnum.getDescByState(appUserWithdrawVO.getAuditStatus()));
            appUserWithdrawVO.setCoinUnitName(CoinUnitEnum.getDescByType(appUserWithdrawVO.getCoinUnit()));
        }
        return JsonResult.successResult(new PageVO<>(iPage.getTotal(), iPage.getPages(),iPage.getRecords()));
    }

    @Override
    public JsonResult check(AuditCheckDTO auditCheckDTO) {
        UpdateWrapper<AppUserWithdraw> wrapper = Wrappers.update();
        wrapper.lambda()
                .set(AppUserWithdraw::getAuditStatus, auditCheckDTO.getState())
                .set(AppUserWithdraw::getAuditTime,new Date())
                .eq(AppUserWithdraw::getId, auditCheckDTO.getId());
        if (auditCheckDTO.getState() == WithdrawStateEnum.CHECK_SUCCESS.getState()) {
            //审核通过 触发提现逻辑
            AppUserWithdraw appUserWithdra = this.getById(auditCheckDTO.getId());
            Wallets wallets = walletMapper.selectOne(new LambdaQueryWrapper<Wallets>().select(Wallets::getId,Wallets::getWalletAmount, Wallets::getBlockAddress).eq(Wallets::getUserId, appUserWithdra.getUserId()));
            wallets.setUserId(appUserWithdra.getUserId());
            this.withdraw(appUserWithdra, wallets);
        }
        this.update(null, wrapper);
        return JsonResult.successResult();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void withdraw(AppUserWithdraw appUserWithdraw, Wallets wallets) {
        CoinUnitEnum coinUnitEnum = CoinUnitEnum.valueOfByType(appUserWithdraw.getCoinUnit());
        BigDecimal fee = appUserWithdraw.getAmount().multiply(coinUnitEnum.getInterestRate());

        //实际提现美元
        BigDecimal actualAmountUSD = appUserWithdraw.getAmount().subtract(fee);
        //换算成提现币种的金额
        BigDecimal actualAmount;
        Integer state;
        if (coinUnitEnum == CoinUnitEnum.LEGAL_CURRENCY) {
            actualAmount = actualAmountUSD.divide(EasyTradeConfig.XPF_2_USD, 7, RoundingMode.FLOOR);
            //法币金额提现
            //TODO 法币充值，走第三方支付接口
            {

            }
            state = WithdrawStateEnum.SUCCESS.getState();
            //把提现的金额往钱包表里余额减去
            UpdateWrapper<Wallets> wrapper = Wrappers.update();
            wrapper.lambda()
                    .set(Wallets::getWalletAmount, wallets.getWalletAmount().subtract(appUserWithdraw.getAmount()))
                    .eq(Wallets::getId, wallets.getId());
            walletMapper.update(null, wrapper);
            //添加钱包流水记录
            WalletLogs walletLogs = WalletLogs.builder()
                    .userId(wallets.getUserId())
                    .amount(appUserWithdraw.getAmount())
                    .action(FlowingActionEnum.EXPENDITURE.getValue())
                    .type(FlowingTypeEnum.WITHDRAWAL.getValue())
                    .build();
            walletLogsMapper.insert(walletLogs);
        } else {
            //TODO Usdt充值 需要通过定时任务去扫描区块链打币有没有成功
            state = WithdrawStateEnum.COIN_STRIKING.getState();
            actualAmount = actualAmountUSD.divide(EasyTradeConfig.USDT_2_USD, 7, RoundingMode.FLOOR);
        }
        appUserWithdraw.setFee(fee);
        appUserWithdraw.setActualAmount(actualAmount);
        appUserWithdraw.setStatus(state);
        this.saveOrUpdate(appUserWithdraw);
    }
}
