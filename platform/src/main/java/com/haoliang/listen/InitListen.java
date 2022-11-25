package com.haoliang.listen;

import com.haoliang.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Dominick Li
 * @description 容器加载后的初始化操作
 **/
@Component
@Slf4j
public class InitListen implements CommandLineRunner {

    @Value("${webSocket.adminwsPath}")
    private String adminwsPath;

    @Override
    public void run(String... args) throws Exception {
        new NettyServer(adminwsPath).start();

    }

}
