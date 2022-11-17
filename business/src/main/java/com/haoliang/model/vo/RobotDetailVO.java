package com.haoliang.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/17 10:17
 **/
@Data
public class RobotDetailVO {
    /**
     * 当前机器人购买金额
     */
    private BigDecimal price;


    private List<RobotVO> robotList;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RobotVO {

        /**
         * 机器人等级
         */
        private int level;
        /**
         * 购买价格
         */
        private BigDecimal price;
        /**
         * 托管金额上限
         */
        private BigDecimal rechargeMax;
        /**
         * 是否持有 true=已购买,false=未购买
         */
        private boolean selected;
    }
}
