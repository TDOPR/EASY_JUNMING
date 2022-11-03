package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName("tree_paths")
public class TreePath implements Serializable  {
    private static final long serialVersionUID = 1L;

    /**
     * 父
     */
    @TableId(type = IdType.AUTO)
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
     * 父等级
     */
    private Integer aLevel;
    /**
     * 子等级
     */
    private Integer dLevel;
    /**
     * 子给父的返佣
     */
    private BigDecimal commission;
    /**
     * 是否团队成员 0是 1否
     */
    private Integer isGroup;

    /**
     * 矿机模块等级
     */
    @TableField(exist = false)
    private Integer mineLevel;
}
