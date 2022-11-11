
package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.model.AppUserWithdraw;
import com.haoliang.model.condition.AppUserWithdrawCondition;
import com.haoliang.model.vo.AppUserWithdrawVO;
import org.apache.ibatis.annotations.Param;


public interface AppUserWithdrawMapper extends BaseMapper<AppUserWithdraw> {

    IPage<AppUserWithdrawVO> page(IPage<AppUserWithdraw> page,@Param("param") AppUserWithdrawCondition searchParam);

}
