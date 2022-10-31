package com.haoliang.controller;

import com.haoliang.common.annotation.RepeatSubmit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Description
 * @Author Dominick Li
 * @CreateTime 2022/10/21 17:14
 **/
@RestController
@RequestMapping("/")
public class TestRepeatSubmitController {

    @GetMapping("/testRepeatSubmit/{id}")
    @RepeatSubmit
    public String testRepeatSubmit(@PathVariable Integer id) {
        return "success";
    }

    public static void main(String[] args) throws Exception {
        ///每秒发送一次请求
        int threadSize = 5;
        Integer successSize = 0;
        for (int i = 0; i < threadSize; i++) {
            RestTemplate restTemplate = new RestTemplate();
            String str = restTemplate.getForObject("http://localhost:9999/testRepeatSubmit/1", String.class);
            if ("success".equals(str)) {
                successSize++;
            }
            Thread.sleep(1000L);
            System.out.println(str);
        }
        System.out.println("总共有" + successSize + "个线程请求成功!");
    }
}
