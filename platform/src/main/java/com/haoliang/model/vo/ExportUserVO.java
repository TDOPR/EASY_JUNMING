package com.haoliang.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.haoliang.common.enums.BooleanEnum;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * @Description 导出用户的模板
 * @Author Dominick Li
 * @CreateTime 2022/10/24 15:24
 **/
@Data
public class ExportUserVO {

    public ExportUserVO(UserVO sysUser) {
        BeanUtils.copyProperties(sysUser,this);
        this.setEnabled(sysUser.getEnabled().equals(BooleanEnum.TRUE.getIntValue()) ?"正常":"禁用");
    }

    @ExcelProperty("用户Id")
    private Integer id;

    @ExcelProperty("账号")
    private String username;

    @ExcelProperty("用户姓名")
    private String name;

    @ExcelProperty("邮箱号")
    private String email;

    @ExcelProperty("手机号")
    private String mobile;

    @ExcelProperty("用户使用状态")
    private String enabled;

    @ExcelProperty("角色名称")
    private String roleName;

//    @ExcelProperty("渠道名称")
//    private String channelName;

}
