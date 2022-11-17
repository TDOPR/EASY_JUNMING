package com.haoliang.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/15 17:25
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyItemVO {
    /**
     * 有效的用户数
     */
    private Integer validUserCount;

    /**
     * 直推用户数
     */
    private Integer aiUserCount;

    /**
     * 直推AI总业绩
     */
    private String aiAmount;

    /**
     * 小团队业绩
     */
    private String minAmount;
    /**
     * 团队总业绩
     */
    private String totalAmount;

}
