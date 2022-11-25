package com.haoliang.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haoliang.enums.FlowingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ViewSelectVO> dateSectionList;

    /**
     * 流水明细
     */
    private List<WalletLogVO> tableList;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ViewSelectVO> typeList;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 类型下拉
     */
    public static List<ViewSelectVO> buildTypeList() {
        List<ViewSelectVO> list = new ArrayList<>();
        list.add(new ViewSelectVO("全部", "-1"));
        list.add(new ViewSelectVO("动态收益存入", "0"));
        for (FlowingTypeEnum flowingTypeEnum : FlowingTypeEnum.values()) {
            if (flowingTypeEnum.getValue() > FlowingTypeEnum.SPECIAL.getValue()) {
                list.add(new ViewSelectVO(FlowingTypeEnum.getWalletDescByValue(flowingTypeEnum.getValue()), flowingTypeEnum.getValue().toString()));
            }
        }
        return list;
    }

}
