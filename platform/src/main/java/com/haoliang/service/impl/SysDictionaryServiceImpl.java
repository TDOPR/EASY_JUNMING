package com.haoliang.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haoliang.common.utils.SpringUtil;
import com.haoliang.config.DictionaryParam;
import com.haoliang.mapper.SysDictionaryMapper;
import com.haoliang.model.SysDictionary;
import com.haoliang.common.model.JsonResult;
import com.haoliang.service.SysDictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Dominick Li
 * @CreateTime 2021/6/18 15:10
 * @description
 **/
@Service
@Slf4j
public class SysDictionaryServiceImpl extends ServiceImpl<SysDictionaryMapper,SysDictionary> implements SysDictionaryService {

    @Value("${spring.redis.topic}")
    private String topicName;

    @Value("${spring.redis.enabled}")
    private boolean enabled;

    @PostConstruct
    public void init() {
        List<SysDictionary> dictionaryList = this.list(new LambdaQueryWrapper<SysDictionary>().eq(SysDictionary::getDicName,"基础字典"));
        if (dictionaryList.size() > 0) {
            DictionaryParam.setLoading(true);
            log.info("初始化把字典信息加入缓存中...");
        }
        dictionaryList.forEach(dictionary -> {
            DictionaryParam.put(dictionary.getDicKey(), dictionary.getDicValue());
        });
    }

    @Override
    public JsonResult modifyBaseDictionary(java.util.Map<String, String> data) {
        SysDictionary dictionary;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            dictionary = this.getOne(new LambdaQueryWrapper<SysDictionary>().eq(SysDictionary::getDicKey,entry.getKey()));
            dictionary.setDicValue(entry.getValue());
            this.saveOrUpdate(dictionary);
            DictionaryParam.put(dictionary.getDicKey(), dictionary.getDicValue());
        }
        if (enabled) {
            RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
            redisTemplate.convertAndSend(topicName, "refreshDictionary");
            log.info("redisTemplate.convertAndSend:{} success", topicName);
        }
        return JsonResult.successResult();
    }

}
