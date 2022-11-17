package com.haoliang.service;

import com.haoliang.common.model.JsonResult;
import com.haoliang.model.dto.AmountDTO;
import com.haoliang.model.vo.TrusteeshipAmountVO;

public interface TrusteeshipService {

    JsonResult recharge(AmountDTO amountDTO, String token);

    JsonResult withdrawal(AmountDTO amountDTO, String token);

    JsonResult<TrusteeshipAmountVO> getTrusteeshipAmount(String token);
}
