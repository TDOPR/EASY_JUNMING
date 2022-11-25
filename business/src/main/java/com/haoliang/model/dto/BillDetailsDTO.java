package com.haoliang.model.dto;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/17 12:02
 **/
@Data
public class BillDetailsDTO extends PageDTO {

    /**
     * -1查询所有 0=代理收益存入 5=充值 6=提现 7=量化收益存入  8=量化投入 9=量化取出
     */
    private Integer type;

    /**
     * 年月 -1=查询所有 '2022-11'
     */
    private String yearMonth;

}
