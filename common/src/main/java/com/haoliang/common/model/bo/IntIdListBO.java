package com.haoliang.common.model.bo;

import lombok.Data;

import java.util.List;

/**
 * @author Dominick Li
 * @CreateTime 2021/7/12 20:06
 * @description
 **/
@Data
public class IntIdListBO {

    /**
     * 唯一标识数组
     */
    private List<Integer> idList;
}
