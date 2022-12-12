package com.haoliang.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.dto.PageDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.model.SysNotice;
import com.haoliang.model.vo.SysNoticeVO;


public interface SysNoticeService extends IService<SysNotice> {

    JsonResult saveNotice(SysNotice sysNotice);

    JsonResult<PageVO<SysNoticeVO>> findMyNoticeList(PageDTO pageDTO);

    JsonResult deleteUserNoticeById(Integer id);
}
