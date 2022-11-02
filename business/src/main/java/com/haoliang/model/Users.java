package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.haoliang.common.base.BaseModel;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 业务用户表
 * @CreateTime 2022/11/1 10:30
 **/
@Data
@TableName("users")
public class Users extends BaseModel {

    /**
     * 邀请码
     */
    private String code;

    /**
     * 邮箱号
     */
    private String eamil;

    /**
     * 用户状态 1=已删除,0=未删除
     */
    private Integer deleted;

    /**
     * 代理商等级
     */
    private Integer level;

    /**
     * 父id  邀请人Id
     */
    private Integer parentId;



}
