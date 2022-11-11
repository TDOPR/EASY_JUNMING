package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCID;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Dominick Li
 * @Description 系统公告
 * @CreateTime 2022/11/7 11:02
 **/
@Data
@TableName("sys_notice")
public class SysNotice extends BaseModelCID {

    /**
     * 排序
     * @required
     */
    @NotNull
    private Integer sortIndex;

    /**
     * 显示状态 1=可见,0=隐藏
     * @required
     */
    @NotNull
    private Integer display;

    /**
     * 中文标题
     * @required
     */
    @NotEmpty
    private String cnTitle;

    /**
     * 中文简介
     * @required
     */
    @NotEmpty
    private String cnDescription;

    /**
     * 中文内容
     * @required
     */
    @NotEmpty
    private String cnText;

    /**
     * 英文标题
     * @required
     */
    @NotEmpty
    private String enTitle;

    /**
     * 英文简介
     * @required
     */
    @NotEmpty
    private String enDescription;

    /**
     * 英文内容
     * @required
     */
    @NotEmpty
    private String enText;


}
