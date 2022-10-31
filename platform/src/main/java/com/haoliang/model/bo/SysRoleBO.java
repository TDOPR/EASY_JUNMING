package com.haoliang.model.bo;

import com.haoliang.model.SysRole;
import lombok.Data;

import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/10/27 10:12
 **/
@Data
public class SysRoleBO  extends SysRole {

    private List<Integer> menuIds;

}
