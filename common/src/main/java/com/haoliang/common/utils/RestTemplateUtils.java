package com.haoliang.common.utils;

import com.haoliang.common.model.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Map;

/**
 * @author Dominick Li
 * @CreateTime 2020/12/1 18:02
 * @description http工具类
 **/
@Component
public class RestTemplateUtils {

    private static RestTemplate restTemplate;

    @Autowired
    SpringUtil springUtil;

    @PostConstruct
    private  void init() {
        restTemplate = SpringUtil.getBean(RestTemplate.class);
    }

    public static JsonResult get(String url) {
        try {
            ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
            if (entity.getStatusCode().value() == 200) {
                return JsonResult.successResult(entity.getBody());
            } else {
                return JsonResult.failureResult(entity.getBody());
            }
        } catch (RestClientException e) {
            return JsonResult.failureResult(e.getMessage());
        }
    }


    public static JsonResult postJson(String url, String jsonData) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> httpEntity = new HttpEntity<>(jsonData, httpHeaders);
        try {
            ResponseEntity<String> entity = restTemplate.postForEntity(url, httpEntity, String.class);
            if (entity.getStatusCode().value() == 200) {
                return JsonResult.successResult(entity.getBody());
            } else {
                return JsonResult.failureResult(entity.getBody());
            }
        } catch (RestClientException e) {
            return JsonResult.failureResult(e.getMessage());
        }
    }

    public static JsonResult post(String url, Map<String, Object> params) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String,Object> multiValueMap = new LinkedMultiValueMap<>();
        for(Map.Entry<String,Object> entry: params.entrySet()){
            multiValueMap.add(entry.getKey(),entry.getValue().toString());
        }
        HttpEntity<Object> httpEntity = new HttpEntity<>(multiValueMap, httpHeaders);
        try {
            ResponseEntity<String> entity = restTemplate.postForEntity(url, httpEntity, String.class);
            if (entity.getStatusCode().value() == 200) {
                return JsonResult.successResult(entity.getBody());
            } else {
                return JsonResult.failureResult(entity.getBody());
            }
        } catch (RestClientException e) {
            e.printStackTrace();
            return JsonResult.failureResult(e.getMessage());
        }
    }


}
