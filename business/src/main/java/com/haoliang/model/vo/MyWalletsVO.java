package com.haoliang.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/16 10:40
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyWalletsVO {

    /**
     * 区块链地址
     */
    private String blockAddress;

    /**
     * 当前余额
     */
    private String balance;

    /**
     * 提现到USDT手续费比例
     */
    private Integer usdtInterestRate;

    /**
     * 提现到法币手续费
     */
    private Integer lcInterestRate;

    /**
     * 我的团队
     */
    private MyItemVO myItem;

    /**
     * 我的量化收益
     */
    private Quantification quantification;

    /**
     * 我的代理收益
     */
    private Proxy proxy;


    /**
     * 量化收益
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Quantification {
        /**
         * 昨天收益
         */
        private String yesterday;
        /**
         * 上周收益
         */
        private String lastWeek;
        /**
         * 上月收益
         */
        private String lastMonth;
        /**
         * 累计收益
         */
        private String total;
    }

    /**
     * 代理收益
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Proxy {

        /**
         * 总收益
         */
        private String totalAmount;

        /**
         * 代数奖
         */
        private String algebra;

        /**
         * 推广奖
         */
        private String rebot;

        /**
         * 团队奖
         */
        private String team;

        /**
         * 特别奖
         */
        private String special;

    }

}
