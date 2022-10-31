package com.haoliang.common.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Dominick Li
 * @description 非自增Id基类无修改时间
 **/
@Data
public class BaseModelNoModifyTime{

    private static final long serialVersionUID = 2863256929817929825L;

    /**
     * id主键，需要手动set Id, 推荐使用雪花算法生成的id， uuid作为主键查询效率低
     */
    @TableId(type = IdType.ASSIGN_ID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 创建时间
     * @ignore
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

}
