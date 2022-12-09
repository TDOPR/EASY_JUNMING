package com.haoliang.scheduled;

import com.haoliang.common.annotation.RedisLock;
import com.haoliang.model.UpdateUserLevelJob;
import com.haoliang.service.AsyncService;
import com.haoliang.service.UpdateUserLevelTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Dominick Li
 * @Description 更新用户的代理商等级定时任务
 * @CreateTime 2022/11/25 11:57
 **/
@Component
@Slf4j
public class UpdateUserLevelScheduledJob {

    @Autowired
    private UpdateUserLevelTaskService updateUserLevelTaskService;

    @Autowired
    private AsyncService asyncService;

    /**
     * 每三分钟拉取下有没有任务需要处理
     */
    //@Scheduled(cron = "* 0/3 * * * ?")
    @Scheduled(fixedDelay = 10000)//测试每10秒执行一次
    @RedisLock
    public void updateUserLevelTask() {
        List<UpdateUserLevelJob> taskList;
        CountDownLatch countDownLatch;
        Integer taskSize;
        try {
            do {
                taskList = updateUserLevelTaskService.findTask(10);
                taskSize = taskList.size();
                if (taskSize > 0) {
                    countDownLatch = new CountDownLatch(taskSize);
                    for (UpdateUserLevelJob updateUserLevelJob : taskList) {
                        asyncService.updateUserLevelTask(updateUserLevelJob.getUserId(), countDownLatch);
                    }
                    countDownLatch.await();
                }
            } while (taskList.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("updateUserLevelTask error:{}", e.getMessage());
        }
    }

}
