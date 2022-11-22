package com.haoliang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haoliang.model.SysNoticeUser;
import com.haoliang.model.vo.SysNoticeVO;

import java.util.List;

public interface SysNoticeUserMapper extends BaseMapper<SysNoticeUser> {

    int insertBatch(Integer noticeId);

    List<SysNoticeVO> findMyNoticeListByUserIdAndCN(Integer userId);

    List<SysNoticeVO> findMyNoticeListByUserIdAndEN(Integer userId);
}
