package com.haoliang.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/22 14:42
 **/
@Data
public class ProxyWalletLogsDetailVO {

    /**
     * 奖励类型
     */
    private List<ViewSelectVO> typeList;

    /**
     * 代理收益
     */
    private List<WalletLogVO> list;

    /**
     * 总页数
     */
    private Integer totalPage;

}
