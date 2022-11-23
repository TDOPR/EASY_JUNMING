package com.haoliang.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 业务
 * @CreateTime 2022/11/23 17:14
 **/
@Data
@Builder
public class BusinessVO {

    /**
     * 今日访问客户数
     */
    private Integer onlineUserSize;

    /**
     * 总客户数
     */
    private Integer totalUserSize;

    /**
     * 总托管量金额
     */
    private String totalTrusteeship;

    /**
     * 总充值金额
     */
    private String totalRecharge;

    /**
     * 总提现金额
     */
    private String totalWithdraw;

    /**
     * 待审核提现任务数
     */
    private Integer ToBeReviewedSize;

}
