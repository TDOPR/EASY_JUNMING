package com.haoliang.model.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/25 17:27
 **/
@Data
public class AppUserRebotDTO {

    private Integer userCount;

    private BigDecimal robotAmount;
}
