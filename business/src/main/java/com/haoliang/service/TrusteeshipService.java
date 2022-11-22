package com.haoliang.service;

import com.haoliang.common.model.JsonResult;
import com.haoliang.model.dto.AmountDTO;
import com.haoliang.model.vo.TrusteeshipAmountVO;

public interface TrusteeshipService {

    /**
     * 托管金额充值
     * @param amountDTO 金额信息
     * @param token 身份凭证
     * @return
     */
    JsonResult recharge(AmountDTO amountDTO, String token);

    /**
     * 托管金额提现
     * @param amountDTO 金额信息
     * @param token 身份凭证
     * @return
     */
    JsonResult withdrawal(AmountDTO amountDTO, String token);

    /**
     * 获取我的托管金额明细
     * @param token 身份凭证
     * @return
     */
    JsonResult<TrusteeshipAmountVO> getTrusteeshipAmount(String token);
}
