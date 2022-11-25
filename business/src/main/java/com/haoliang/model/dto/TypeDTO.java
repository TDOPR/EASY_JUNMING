package com.haoliang.model.dto;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/24 17:38
 **/
@Data
public class TypeDTO extends PageDTO {

    /**
     * 是否发放给用户 1=已发放, 0=未发放
     */
    private Integer type = -1;

}
