package com.haoliang.model.vo;

import com.haoliang.enums.FlowingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/17 18:11
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletLogsDetailVO {

    /**
     * 日期下拉列表
     */
    private List<ViewSelectVO> dateSectionList;

    /**
     * 流水明细
     */
    private List<WalletLogVO> walletLogList;

    /**
     * 存入金额 (量化 + 代理  + 充值)
     */
    private String deposit;

    /**
     * 取出金额
     */
    private String takeOut;

    /**
     * 类型下拉
     */
    private List<ViewSelectVO> typeList = Arrays.asList(
            new ViewSelectVO("全部", "-1"),
            new ViewSelectVO(FlowingTypeEnum.RECHARGE),
            new ViewSelectVO(FlowingTypeEnum.WITHDRAWAL),
            new ViewSelectVO("代理收益存入", "0"),
            new ViewSelectVO("量化收益存入", FlowingTypeEnum.STATIC.getValue().toString()),
            new ViewSelectVO(FlowingTypeEnum.ENTRUSTMENT),
            new ViewSelectVO(FlowingTypeEnum.WITHDRAWL_WALLET));
}
