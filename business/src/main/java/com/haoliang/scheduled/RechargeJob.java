//package com.haoliang.scheduled;
//
//import com.haoliang.manager.TradeManager;
//import com.haoliang.utils.TaskRedisCheckKey;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Conditional;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.time.Duration;
//
//@Component
//@Slf4j
//@EnableScheduling
//public class RechargeJob {
//
//    @Autowired
//    private StringRedisTemplate redisTemplate;
//
//    @Resource
//    private TradeManager tradeManager;
//
//    @Scheduled(cron = "0/3 * * * * ?")
//    public void rechargeEvent() {
//        try {
//            boolean bool = redisTemplate.opsForValue().setIfAbsent(TaskRedisCheckKey.ANALYZE_EVENT, "", Duration.ofSeconds(50));
//            if(bool){
//                tradeManager.rechargeEvent();
//
//            }
//        }
//    }
//
//
//
//
//}
