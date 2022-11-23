package com.haoliang.common.aspect;

import com.alibaba.fastjson.JSONObject;
import com.haoliang.common.annotation.RepeatSubmit;
import com.haoliang.common.constant.CacheKeyPrefixConstants;
import com.haoliang.common.util.JwtTokenUtil;
import com.haoliang.common.util.encrypt.MD5Util;
import com.haoliang.common.util.redis.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collection;
import java.util.Map;

/**
 * @author Dominick Li
 * @CreateTime 2022/10/12 20:24
 * @description 防重复提交
 **/
@Slf4j
@Aspect
@Configuration
@ConditionalOnProperty(name = "app.enableRepeatSubmit", havingValue = "true")
public class RepeatSubmitAspect {

    @Pointcut("@annotation(com.haoliang.common.annotation.RepeatSubmit)")
    public void repeatSubmitPointCut() {
    }

    @Around("repeatSubmitPointCut()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Method method = currentMethod(proceedingJoinPoint);
        //获取到方法的注解对象
        RepeatSubmit repeatSubmit = method.getAnnotation(RepeatSubmit.class);
        // 如果注解值大于0则使用注解里的值
        long interval = 1000;
        if (repeatSubmit.interval() > 0) {
            interval = repeatSubmit.interval();
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String nowParams = argsToString(proceedingJoinPoint.getArgs());

        // 请求地址（作为存放cache的key值）
        String url = request.getRequestURI();

        // 用户的唯一标识
        String token = request.getHeader(JwtTokenUtil.TOKEN_NAME);
        // 唯一标识（指定key + url + 消息头）
        String submitKey = CacheKeyPrefixConstants.REPEAT_SUBMIT+ MD5Util.toMD5(url + "_" + token + "_" + nowParams);

        boolean flag = false;

        //判断缓存中是否有此key
        if (!RedisUtil.hasKey(submitKey)) {
            log.info("key={},interval={},非重复提交", submitKey, interval);
            //如果没有表示不是重复提交并设置key时间
            RedisUtil.setCacheObject(submitKey, "", Duration.ofMillis(interval));
            flag = true;
        }

        if (flag) {
            Object result = null;
            try {
                result = proceedingJoinPoint.proceed();
            } catch (Throwable e) {
                /*异常通知方法*/
                log.error("异常通知方法>目标方法名{},异常为：{}", method.getName(), e);
            }
            return result;
        } else {
            return "{'code':500,'msg':'请勿重复提交'}";
        }
    }


    /**
     * 根据切入点获取执行的方法
     */
    private Method currentMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        //获取目标类的所有方法，找到当前要执行的方法
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        Method resultMethod = null;
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                resultMethod = method;
                break;
            }
        }
        return resultMethod;
    }

    /**
     * 参数拼装
     */
    private String argsToString(Object[] paramsArray) {
        StringBuilder params = new StringBuilder();
        if (paramsArray != null && paramsArray.length > 0) {
            for (Object o : paramsArray) {
                if (!ObjectUtils.isEmpty(o) && !isFilterObject(o)) {
                    try {
                        params.append(JSONObject.toJSONString(o)).append(" ");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return params.toString().trim();
    }

    /**
     * 判断是否是需要过滤的对象
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
                || o instanceof BindingResult;
    }

}
