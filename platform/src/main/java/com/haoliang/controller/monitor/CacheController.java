//package com.haoliang.controller.monitor;
//
//import cn.hutool.core.collection.CollUtil;
//import com.alibaba.fastjson.JSONObject;
//import com.haoliang.common.constant.CacheKeyPrefixConstants;
//import com.haoliang.common.model.JsonResult;
//import com.haoliang.common.utils.redis.CacheUtils;
//import com.haoliang.common.utils.redis.RedisUtils;
//import com.haoliang.model.vo.SysCacheVO;
//import io.micrometer.core.instrument.util.JsonUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.redisson.spring.data.connection.RedissonConnectionFactory;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * 缓存列表
// *
// * @author Dominick Li
// * @Description
// * @CreateTime 2022/11/7 17:25
// **/
//@RestController
//@RequestMapping("/cache")
//public class CacheController {
//
//
//    private final RedissonConnectionFactory connectionFactory;
//
//    private final static List<SysCacheVO> CACHES = new ArrayList<>();
//
//    static {
//        CACHES.add(new SysCacheVO(CacheKeyPrefixConstants.DISTRIBUTED_LOCK, "分布式锁"));
//        CACHES.add(new SysCacheVO(CacheKeyPrefixConstants.TOKEN, "在线用户"));
//        CACHES.add(new SysCacheVO(CacheKeyPrefixConstants.APP_TOKEN, "配置信息"));
//        CACHES.add(new SysCacheVO(CacheKeyPrefixConstants.CAPTCHA_CODE, "验证码"));
//        CACHES.add(new SysCacheVO(CacheKeyPrefixConstants.REPEAT_SUBMIT, "防重提交"));
//        CACHES.add(new SysCacheVO(CacheKeyPrefixConstants.RATE_LIMIT, "限流处理"));
//        CACHES.add(new SysCacheVO(CacheKeyPrefixConstants.PWD_ERROR_COUNT, "密码错误次数"));
//        CACHES.add(new SysCacheVO(CacheKeyPrefixConstants.SYS_MENU + ":", "系统菜单"));
//    }
//
//    /**
//     * 获取缓存监控列表
//     */
//    @PreAuthorize("hasAuthority('sys:cache:list')")
//    @GetMapping()
//    public JsonResult<Map<String, Object>> getInfo() throws Exception {
//        RedisConnection connection = connectionFactory.getConnection();
//        Properties info = connection.info();
//        Properties commandStats = connection.info("commandstats");
//        Long dbSize = connection.dbSize();
//
//        Map<String, Object> result = new HashMap<>(3);
//        result.put("info", info);
//        result.put("dbSize", dbSize);
//
//        List<Map<String, String>> pieList = new ArrayList<>();
//        if (commandStats != null) {
//            commandStats.stringPropertyNames().forEach(key -> {
//                Map<String, String> data = new HashMap<>(2);
//                String property = commandStats.getProperty(key);
//                data.put("name", StringUtils.removeStart(key, "cmdstat_"));
//                data.put("value", StringUtils.substringBetween(property, "calls=", ",usec"));
//                pieList.add(data);
//            });
//        }
//        result.put("commandStats", pieList);
//        return JsonResult.successResult(result);
//    }
//
//    /**
//     * 获取缓存监控缓存名列表
//     */
//    @PreAuthorize("hasAuthority('sys:cache:list')")
//    @GetMapping("/getNames")
//    public JsonResult<List<SysCacheVO>> cache() {
//        return JsonResult.successResult(CACHES);
//    }
//
//    /**
//     * 获取缓存监控Key列表
//     *
//     * @param cacheName 缓存名
//     */
//    @PreAuthorize("hasAuthority('sys:cache:list')")
//    @GetMapping("/getKeys/{cacheName}")
//    public JsonResult<Collection<String>> getCacheKeys(@PathVariable String cacheName) {
//        Collection<String> cacheKeys = new HashSet<>(0);
//        if (isCacheNames(cacheName)) {
//            Set<Object> keys = CacheUtils.keys(cacheName);
//            if (CollUtil.isNotEmpty(keys)) {
//                cacheKeys = keys.stream().map(Object::toString).collect(Collectors.toList());
//            }
//        } else {
//            cacheKeys = RedisUtils.keys(cacheName + "*");
//        }
//        return JsonResult.successResult(cacheKeys);
//    }
//
//    /**
//     * 获取缓存监控缓存值详情
//     *
//     * @param cacheName 缓存名
//     * @param cacheKey  缓存key
//     */
//    @PreAuthorize("hasAuthority('sys:cache:list')")
//    @GetMapping("/getValue/{cacheName}/{cacheKey}")
//    public JsonResult<SysCacheVO> getCacheValue(@PathVariable String cacheName, @PathVariable String cacheKey) {
//        Object cacheValue;
//        if (isCacheNames(cacheName)) {
//            cacheValue = CacheUtils.get(cacheName, cacheKey);
//        } else {
//            cacheValue = RedisUtils.getCacheObject(cacheKey);
//        }
//        SysCacheVO sysCache = new SysCacheVO(cacheName, cacheKey, JSONObject.toJSONString(cacheValue));
//        return JsonResult.successResult(sysCache);
//    }
//
//    /**
//     * 清理缓存监控缓存名
//     *
//     * @param cacheName 缓存名
//     */
//    @PreAuthorize("hasAuthority('sys:cache:list')")
//    @DeleteMapping("/clearCacheName/{cacheName}")
//    public JsonResult<Void> clearCacheName(@PathVariable String cacheName) {
//        if (isCacheNames(cacheName)) {
//            CacheUtils.clear(cacheName);
//        } else {
//            RedisUtils.deleteObjects(cacheName + "*");
//        }
//        return JsonResult.successResult();
//    }
//
//    /**
//     * 清理缓存监控Key
//     *
//     * @param cacheKey key名
//     */
//    @PreAuthorize("hasAuthority('sys:cache:list')")
//    @DeleteMapping("/clearCacheKey/{cacheName}/{cacheKey}")
//    public JsonResult<Void> clearCacheKey(@PathVariable String cacheName, @PathVariable String cacheKey) {
//        if (isCacheNames(cacheName)) {
//            CacheUtils.evict(cacheName, cacheKey);
//        } else {
//            RedisUtils.deleteObject(cacheKey);
//        }
//        return JsonResult.successResult();
//    }
//
//    /**
//     * 清理全部缓存监控
//     */
//    @PreAuthorize("hasAuthority('sys:cache:list')")
//    @DeleteMapping("/clearCacheAll")
//    public JsonResult<Void> clearCacheAll() {
//        RedisUtils.deleteObjects("*");
//        return JsonResult.successResult();
//    }
//
//    private boolean isCacheNames(String cacheName) {
//        return !StringUtils.contains(cacheName, ":");
//    }
//
//}
