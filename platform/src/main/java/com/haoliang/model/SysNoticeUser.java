package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Dominick Li
 * @Description 公告和用户之前的引用关系
 * @CreateTime 2022/11/22 10:27
 **/
@Data
@TableName("sys_notice_user")
public class SysNoticeUser  {

    /**
     * 公告Id
     */
    private Integer noticeId;

    /**
     * app用户Id
     */
    private Integer userId;

}
