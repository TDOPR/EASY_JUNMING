package com.haoliang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.mapper.UpdateUserLevelTaskMapper;
import com.haoliang.model.UpdateUserLevelJob;
import com.haoliang.model.dto.AppUserTreeDTO;
import com.haoliang.service.UpdateUserLevelTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/11/25 11:17
 **/
@Service
public class UpdateUserLevelTaskServiceImpl extends ServiceImpl<UpdateUserLevelTaskMapper, UpdateUserLevelJob> implements UpdateUserLevelTaskService {

    @Resource
    private UpdateUserLevelTaskMapper updateUserLevelTaskMapper;

    /**
     * 任务延迟处理的时间
     */
    private final Integer DELAY_MINUTE = 5;

    @Override
    public void insertListByUserId(Integer userId) {
        List<AppUserTreeDTO> treeDTOList = updateUserLevelTaskMapper.findUserTreeByUserId(userId);
        List<UpdateUserLevelJob> updateUserLevelJobList = new ArrayList<>();
        UpdateUserLevelJob updateUserLevelJob;
        LocalDateTime now = LocalDateTime.now();
        for (AppUserTreeDTO appUserTreeDTO : treeDTOList) {
            if (appUserTreeDTO.getPid() > 0) {
                //插入需要更新代理商等级的用户记录
                updateUserLevelJob = this.getOne(new LambdaQueryWrapper<UpdateUserLevelJob>().eq(UpdateUserLevelJob::getUserId, appUserTreeDTO.getPid()));
                if (updateUserLevelJob == null) {
                    //如果不存在,则插入数据
                    updateUserLevelJobList.add(new UpdateUserLevelJob(appUserTreeDTO.getPid(), now, DELAY_MINUTE));
                }
            }
        }
        if (updateUserLevelJobList.size() > 0) {
            this.saveBatch(updateUserLevelJobList);
        }
    }

    @Override
    public List<UpdateUserLevelJob> findTask(Integer pageSize) {
        Page<UpdateUserLevelJob> updateUserLevelTaskPage=new Page<UpdateUserLevelJob>(1, pageSize);
        IPage<UpdateUserLevelJob> page = this.page(updateUserLevelTaskPage,
                new LambdaQueryWrapper<UpdateUserLevelJob>()
                        .le(UpdateUserLevelJob::getDelayTime, LocalDateTime.now())
                        .orderByAsc(UpdateUserLevelJob::getDelayTime));
        return page.getRecords();
    }
}
