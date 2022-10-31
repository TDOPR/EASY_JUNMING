package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.model.SysUser;
import com.haoliang.model.vo.UserVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SysUserMapper extends BaseMapper<SysUser> {

    List<String> findAllByUsernameByRoleId(@Param("roleId")Integer roleId);

    IPage<UserVO> selectPageVo(IPage<SysUser> page,@Param("username") Object username,@Param("beginDate") Date beginDate,@Param("endDate") Date endDate);

}
