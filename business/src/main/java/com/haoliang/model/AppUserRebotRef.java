package com.haoliang.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Dominick Li
 * @Description 用户机器人邀请关系
 * @CreateTime 2022/11/10 17:52
 **/
@Data
@Builder
@TableName("app_user_rebot_ref")
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRebotRef {

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * 被邀请的用户Id
     */
    private Integer inviteUserId;

    /**
     * 被邀请的用户是用户的第几位购买机器人
     */
    private Integer level;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;


}
