package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.model.SysNotice;
import com.haoliang.model.vo.SysNoticeVO;

import java.util.List;

public interface SysNoticeService extends IService<SysNotice> {
    JsonResult saveNotice(SysNotice sysNotice);

    JsonResult<List<SysNoticeVO>> findMyNoticeList(String token, Integer type);

    JsonResult deleteUserNoticeById(String token, Integer id);
}
