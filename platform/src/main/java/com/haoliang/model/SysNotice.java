package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModelCID;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Dominick Li
 * @Description 系统公告  TODO  需要新增西班牙语和葡萄牙语
 * @CreateTime 2022/11/7 11:02
 **/
@Data
@TableName("sys_notice")
public class SysNotice extends BaseModelCID {

    /**
     * 排序
     * @required
     */
    private Integer sortIndex=0;

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
     * 中文内容
     * @required
     */
    @NotEmpty
    private String cnText;

    /**
     * 中文简介
     * @required
     */
    @NotEmpty
    private String cnDescription;

    /**
     * 英文标题
     * @required
     */
    @NotEmpty
    private String enTitle;

    /**
     * 英文内容
     * @required
     */
    @NotEmpty
    private String enText;

    /**
     * 英文简介
     * @required
     */
    @NotEmpty
    private String enDescription;

    /**
     * 西班牙语标题
     */
    @NotEmpty
    private String esTitle;

    /**
     * 西班牙语内容
     */
    @NotEmpty
    private String esText;

    /**
     * 西班牙语简介
     */
    @NotEmpty
    private String esDescription;

    /**
     * 葡萄牙语标题
     */
    private String ptTitle;

    /**
     * 葡萄牙语内容
     */
    private String ptText;

    /**
     * 葡萄牙语简介
     */
    private String ptDescription;

}
