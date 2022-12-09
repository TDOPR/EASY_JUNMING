package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.model.ThreadLocalManager;
import com.haoliang.common.model.dto.TypeDTO;
import com.haoliang.common.model.vo.PageVO;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.mapper.SysNoticeMapper;
import com.haoliang.mapper.SysNoticeUserMapper;
import com.haoliang.model.SysNotice;
import com.haoliang.model.SysNoticeUser;
import com.haoliang.model.vo.SysNoticeVO;
import com.haoliang.service.SysNoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/7 11:20
 **/
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Resource
    private SysNoticeUserMapper sysNoticeUserMapper;

    @Override
    public JsonResult saveNotice(SysNotice sysNotice) {
        //是否为新增公告
        boolean add = sysNotice.getId() == null;
        this.saveOrUpdate(sysNotice);
        if (add) {
            //插入公告和用户关联表
            sysNoticeUserMapper.insertBatch(sysNotice.getId());
        }
        return JsonResult.successResult();
    }

    @Override
    public JsonResult<PageVO<SysNoticeVO>> findMyNoticeList(TypeDTO type) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        Page page = new Page<>(type.getCurrentPage(), type.getPageSize());
        IPage iPage;
        if (type.getType() == 1) {
            iPage = sysNoticeUserMapper.findMyNoticeListByUserIdAndCN(page, userId);
        } else {
            iPage = sysNoticeUserMapper.findMyNoticeListByUserIdAndEN(page, userId);
        }
        return JsonResult.successResult(new PageVO<>(iPage));
    }

    @Override
    public JsonResult deleteUserNoticeById(Integer id) {
        Integer userId = JwtTokenUtil.getUserIdFromToken(ThreadLocalManager.getToken());
        sysNoticeUserMapper.delete(new LambdaQueryWrapper<SysNoticeUser>()
                .eq(SysNoticeUser::getNoticeId, id)
                .eq(SysNoticeUser::getUserId, userId)
        );
        return JsonResult.successResult();
    }
}
