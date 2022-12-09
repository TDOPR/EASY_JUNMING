package com.haoliang.service;

import com.haoliang.common.model.JsonResult;
import com.haoliang.model.dto.AmountDTO;
import com.haoliang.model.vo.TrusteeshipAmountVO;

public interface TrusteeshipService {

    /**
     * 托管金额充值
     * @param amountDTO 金额信息
     * @return
     */
    JsonResult recharge(AmountDTO amountDTO);

    /**
     * 托管金额提现
     * @param amountDTO 金额信息
     * @return
     */
    JsonResult withdrawal(AmountDTO amountDTO);

    /**
     * 获取我的托管金额明细
     * @return
     */
    JsonResult<TrusteeshipAmountVO> getTrusteeshipAmount();
}
