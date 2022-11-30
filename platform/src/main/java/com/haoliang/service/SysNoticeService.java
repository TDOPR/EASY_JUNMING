package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.dto.TypeDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.model.SysNotice;
import com.haoliang.model.vo.SysNoticeVO;


public interface SysNoticeService extends IService<SysNotice> {

    JsonResult saveNotice(SysNotice sysNotice);

    JsonResult<PageVO<SysNoticeVO>> findMyNoticeList(String token, TypeDTO type);

    JsonResult deleteUserNoticeById(String token, Integer id);
}
