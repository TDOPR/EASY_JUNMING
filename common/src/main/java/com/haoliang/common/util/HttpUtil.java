package com.haoliang.common.util;


import okhttp3.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Dominick Li
 * @createTime 2020/3/19 18:33
 * @description http请求工具类
 **/
public class HttpUtil {

    /**
     * 需要导入okhttp依赖
     * <dependency>
     * <groupId>com.squareup.okio</groupId>
     * <artifactId>okio</artifactId>
     * <version>1.13.0</version>
     * </dependency>
     * <dependency>
     * <groupId>com.squareup.okhttp3</groupId>
     * <artifactId>okhttp</artifactId>
     * <version>3.12.0</version>
     * </dependency>
     */
    private HttpUtil() {
    }

    /**
     * post请求
     *
     * @param url      接口路径
     * @param jsonData json数据
     */
    public static String postJson(String url, String jsonData) {
        OkHttpClient client = getInstance();
        Request.Builder builder = new Request.Builder().url(url);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        builder.post(requestBody);
        try {
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String postJson(String url, String headerKey,String headerValue, String jsonData) {
        OkHttpClient client = getInstance();
        Request.Builder builder = new Request.Builder().url(url);
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), jsonData);
        builder.post(requestBody);
        builder.addHeader(headerKey,headerValue);
        try {
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * post请求
     *
     * @param url   接口路径
     * @param param key value数据
     */
    public static String post(String url, Map<String, String> param) {
        OkHttpClient client = getInstance();
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        FormBody mBody = builder.build();
        try {
            URL realUrl = new URL(url);
            Request request = new Request.Builder().url(realUrl).post(mBody).build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get请求
     *
     * @param url 接口路径
     */
    public static String get(String url) {
        OkHttpClient client = getInstance();
        try {
            URL realUrl = new URL(url);
            Request request = new Request.Builder().url(realUrl).get().build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            return result;
        } catch (Exception e) {
            return null;
        }
    }


    public static OkHttpClient getInstance() {
        //设置连接,写入,读取超时时间
        return LazyLoadHttpClient.INSTANCE;
    }

    private static class LazyLoadHttpClient {

        /**
         * 连接超时时间
         */
        private final static int DEFAULT_CONNECT_TIMEOUT = 15;

        /**
         * 写入超时时间
         */
        private final static int DEFAULT_WRITE_TIMEOUT = 30;

        /**
         * 读取超时时间
         */
        private final static int DEFAULT_READ_TIMEOUT = 60;

        /**
         * 复用连接池
         */
        private static final ConnectionPool CONNECTION_POOL = new ConnectionPool(256, 5L, TimeUnit.MINUTES);

        private static OkHttpClient INSTANCE = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .connectionPool(CONNECTION_POOL)
                .build();
    }

}


