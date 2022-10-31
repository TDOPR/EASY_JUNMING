package com.haoliang.model.vo;

import com.haoliang.model.SysUser;
import lombok.Data;


/**
 * @author Dominick Li
 * @CreateTime 2021/5/14 9:58
 * @description
 **/
@Data
public class UserVO extends SysUser {

    private String channelName;

    private String roleName;

}
