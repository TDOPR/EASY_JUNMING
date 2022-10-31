package com.haoliang.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author Dominick Li
 * @description 自增Id基类无修改时间
 **/
@Data
public  class BaseModelCIDNoModifyTime{

    private static final long serialVersionUID = 2863256929817929827L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;


}
