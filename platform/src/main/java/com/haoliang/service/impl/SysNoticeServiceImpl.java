package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.model.JsonResult;
import com.haoliang.common.utils.JwtTokenUtils;
import com.haoliang.mapper.SysNoticeMapper;
import com.haoliang.mapper.SysNoticeUserMapper;
import com.haoliang.model.SysNotice;
import com.haoliang.model.SysNoticeUser;
import com.haoliang.model.vo.SysNoticeVO;
import com.haoliang.service.SysNoticeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
    public JsonResult<List<SysNoticeVO>> findMyNoticeList(String token, Integer type) {
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        List<SysNoticeVO> list;
        if (type == 1) {
            list = sysNoticeUserMapper.findMyNoticeListByUserIdAndCN(userId);
        } else {
            list = sysNoticeUserMapper.findMyNoticeListByUserIdAndEN(userId);
        }
        return JsonResult.successResult(list);
    }

    @Override
    public JsonResult deleteUserNoticeById(String token, Integer id) {
        Integer userId = JwtTokenUtils.getUserIdFromToken(token);
        sysNoticeUserMapper.delete(new LambdaQueryWrapper<SysNoticeUser>()
                .eq(SysNoticeUser::getNoticeId, id)
                .eq(SysNoticeUser::getUserId, userId)
        );
        return JsonResult.successResult();
    }
}
