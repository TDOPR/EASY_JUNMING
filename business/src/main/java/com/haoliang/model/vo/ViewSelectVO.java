package com.haoliang.model.vo;

import com.haoliang.enums.FlowingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/17 16:39
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewSelectVO {

    private String lable;

    private String value;

    public ViewSelectVO(FlowingTypeEnum flowingTypeEnum){
        this.lable=flowingTypeEnum.getDesc();
        this.value=flowingTypeEnum.getValue().toString();
    }

}
