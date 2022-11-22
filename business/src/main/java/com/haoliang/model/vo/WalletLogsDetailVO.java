package com.haoliang.model.vo;

import com.haoliang.enums.FlowingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private Map<String,List<WalletLogVO>> walletLogMap;

    /**
     * 存入金额 (量化 + 代理  + 充值)
     */
    private String deposit;

    /**
     * 取出金额
     */
    private String takeOut;

    /**
     * 流水类型
     */
    private List<ViewSelectVO> typeList;

    /**
     * 类型下拉
     */
    public static List<ViewSelectVO> buildTypeList(List<Integer> typeList) {
        List<ViewSelectVO> list = new ArrayList<>();
        list.add(new ViewSelectVO("全部", "-1"));
        for (Integer type : typeList) {
            list.add(new ViewSelectVO(FlowingTypeEnum.getWalletDescByValue(type), type.toString()));
        }
        return list;
    }

}
