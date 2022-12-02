package com.haoliang.model.play;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description 交易信息
 * @Author Dominick Li
 * @CreateTime 2022/12/1 17:57
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    /**
     * 充值的货币类型
     */
    private String currency;

    /**
     * 交换的货币类型
     */
    private String exchange_currency;

    /**
     * 充值金额
     */
    private String value;

    /**
     * 来自充值的银行账号
     */
    private String from_bank_account_id;

    /**
     * 充值到具体某个银行的账号
     */
    private String to_bank_account_id;

    /**
     *参与交易的个人或公司的 ID。
     */
    private String subject_id;


}
