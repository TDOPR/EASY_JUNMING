package com.haoliang.model.dto;

import lombok.Data;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/18 10:16
 **/
@Data
public class AppUserDTO {


    /**
     * 个性签名
     */
    private String autograph;

    /**
     * 用户昵称
     */
    private String nickName;

}
