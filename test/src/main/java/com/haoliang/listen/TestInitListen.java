package com.haoliang.listen;

import com.haoliang.common.utils.redis.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Dominick Li
 * @description 容器加载后的初始化操作
 **/
@Component
@Slf4j
public class TestInitListen implements CommandLineRunner {

    @Override
    public void run(String... args) {
        RedisUtils.setCacheObject("shop:number", 5);
    }

}
