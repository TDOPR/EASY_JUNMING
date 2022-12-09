package com.haoliang.common.filter;

import com.haoliang.common.model.ThreadLocalManager;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dominick Li
 * @Description
 * @CreateTime 2022/12/8 17:59
 **/
@Component
public class HttpInterceptor  implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalManager.remove();
        System.out.println("释放资源");
        return;
    }

}
