package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 */
@Data
@TableName("tree_paths")
public class TreePath implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 父
     */
    private Integer ancestor;
    /**
     * 子
     */
    private Integer descendant;
    /**
     * 子是父的几级
     */
    private Integer level;

    /**
     * 创建时间
     * @ignore
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
//    /**
//     * 父等级
//     */
//    private Integer aLevel;
//    /**
//     * 子等级
//     */
//    private Integer dLevel;
//    /**
//     * 子给父的返佣
//     */
//    private BigDecimal commission;


}
