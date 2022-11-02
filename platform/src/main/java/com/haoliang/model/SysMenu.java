package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.haoliang.common.base.BaseModelCID;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;


/**
 * @author Dominick Li
 * @description 系统菜单表
 **/
@Data
@TableName("sys_menu")
public class SysMenu extends BaseModelCID {

    /**
     * 访问路径
     * @required
     */
    @NotEmpty(message = "不能为空")
    private String path;

    /**
     * 菜单名称
     * @required
     */
    @NotEmpty(message = "不能为空")
    private String title;

    /**
     * 菜单图标样式
     * @required
     */
    @NotEmpty(message = "不能为空")
    private String icon;

    /**
     * 父菜单编号
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer parentId;

    /**
     * 排序下标
     * @required
     */
    @NotEmpty(message = "不能为空")
    private Integer sortIndex = 1;

    /**
     * 组件路径
     * @required
     */
    @NotEmpty(message = "不能为空")
    private String importStr;

    /**
     * 子菜单列表
     */
    @TableField(exist = false)
    private List<SysMenu> children;

}
