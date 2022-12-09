package com.haoliang.controller;

import com.haoliang.common.annotation.RepeatSubmit;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.dto.AmountDTO;
import com.haoliang.model.vo.TrusteeshipAmountVO;
import com.haoliang.service.TrusteeshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 托管量化
 * @author Dominick Li
 * @CreateTime 2022/11/17 11:09
 **/
@RestController
@RequestMapping("/trusteeship")
public class TrusteeshipController {

    @Autowired
    private TrusteeshipService trusteeshipRecharge;

    /**
     * 我的量化金额
     */
    @GetMapping
    public JsonResult<TrusteeshipAmountVO> quantificationAmount(){
        return trusteeshipRecharge.getTrusteeshipAmount();
    }

    /**
     * 托管金额充值
     */
    @RepeatSubmit
    @PostMapping("/recharge")
    public JsonResult entrustWithdrawal(@Valid @RequestBody AmountDTO amountDTO){
        return trusteeshipRecharge.recharge(amountDTO);
    }

    /**
     * 托管金额提现到钱包
     */
    @RepeatSubmit
    @PostMapping("/withdrawal")
    public JsonResult trusteeshipWithdrawal(@Valid @RequestBody AmountDTO amountDTO){
        return trusteeshipRecharge.withdrawal(amountDTO);
    }

}
