package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.enums.FlowingActionEnum;
import com.haoliang.enums.FlowingTypeEnum;
import com.haoliang.model.WalletLogs;
import com.haoliang.model.dto.BillDetailsDTO;
import com.haoliang.common.model.dto.TypeDTO;
import com.haoliang.model.vo.ProfitLogsDetailVO;
import com.haoliang.model.vo.ProxyWalletLogsDetailVO;
import com.haoliang.model.vo.WalletLogVO;
import com.haoliang.model.vo.WalletLogsDetailVO;

import java.math.BigDecimal;
import java.util.List;

public interface WalletLogsService extends IService<WalletLogs> {

    /**
     * 插入流水记录
     * @param userId  用户Id
     * @param amount  变更的金额
     * @param flowingActionEnum 收入或支出
     * @param flowingTypeEnum 流水类型
     * @return 执行结果
     */
    boolean insertWalletLogs(Integer userId,BigDecimal amount, FlowingActionEnum flowingActionEnum, FlowingTypeEnum flowingTypeEnum);

    /**
     * 根据流水类型查询流水明细
     * @param userId 用户Id
     * @param type 流水类型
     * @return 明细记录
     */
    JsonResult<List<WalletLogVO>> listByUserIdAndType(Integer userId,Integer type);

    /**
     * 获取代理奖
     * @param userId
     * @return
     */
    List<WalletLogs> getMyProxyWalletLogs(Integer userId);

    /**
     * 获取我的钱包账单明细
     * @return
     */
    JsonResult<WalletLogsDetailVO> getMybillDetails(BillDetailsDTO billDetailsDTO);

    /**
     * 获取量化奖励明细
     * @return
     */
    JsonResult<ProfitLogsDetailVO> quantificationDetail(TypeDTO typeDTO);

    /**
     * 获取动态奖励明细
     * @return
     */
    JsonResult<ProxyWalletLogsDetailVO> proxyDetail(TypeDTO typeDTO);
}
