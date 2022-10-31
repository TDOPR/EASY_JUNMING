package com.haoliang.model.vo;

import com.haoliang.model.SysUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/10/27 9:39
 **/
@Data
public class UserVO2 {
    private Integer id;
    private Integer roleId;
    private Integer channelId;
    private String username;

    public UserVO2(SysUser sysUser){
        BeanUtils.copyProperties(sysUser,this);
    }
}
