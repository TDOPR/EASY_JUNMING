package com.haoliang.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/18 17:06
 **/
@Data
public class ProfitLogsDetailVO {

    public ProfitLogsDetailVO(String settled, String noSettled, List<ProfitLogsVO> list) {
        this.settled = settled;
        this.noSettled = noSettled;
        this.list = list;
    }

    /**
     * 已结算
     */
    private String settled;

    /**
     * 未结算
     */
    private String noSettled;

    private List<ProfitLogsVO> list;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfitLogsVO {

        /**
         * 流水日期
         */
        private String createTime;

        /**
         * 本金
         */
        private String principal;

        /**
         * 产生的收益金额
         */
        private String generatedAmount;

        /**
         * 是否交割  1=已交割 0=未交割
         */
        private Integer status;

    }
}
