
package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.model.EvmWithdraw;
import com.haoliang.model.condition.AppUserWithdrawCondition;
import com.haoliang.model.vo.EvmWithdrawVO;
import org.apache.ibatis.annotations.Param;


public interface EvmWithdrawMapper extends BaseMapper<EvmWithdraw> {

    IPage<EvmWithdrawVO> page(IPage<EvmWithdraw> page, @Param("param") AppUserWithdrawCondition searchParam);

}
