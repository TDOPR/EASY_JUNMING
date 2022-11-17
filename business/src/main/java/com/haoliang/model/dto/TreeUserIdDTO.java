package com.haoliang.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Dominick Li
 * @Description 用户关系树结构
 * @CreateTime 2022/11/14 17:30
 **/
@Data
public class TreeUserIdDTO {

    private Integer userId;

    private List<Integer> childIdList;

}
