package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haoliang.model.SysNoticeUser;
import com.haoliang.model.SysUser;
import com.haoliang.model.vo.SysNoticeVO;
import org.apache.ibatis.annotations.Param;


public interface SysNoticeUserMapper extends BaseMapper<SysNoticeUser> {

    int insertBatch(Integer noticeId);

    IPage<SysNoticeVO> findMyNoticeListByUserIdAndCN(IPage<SysUser> page,@Param("userId")Integer userId);

    IPage<SysNoticeVO> findMyNoticeListByUserIdAndEN(IPage<SysUser> page,@Param("userId")Integer userId);
}
