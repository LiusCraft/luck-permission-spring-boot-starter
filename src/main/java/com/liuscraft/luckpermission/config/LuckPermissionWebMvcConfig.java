package com.liuscraft.luckpermission.config;

import com.liuscraft.luckpermission.LuckPermission;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;
import com.liuscraft.luckpermission.interceptors.VerifyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author LiusCraft
 * @date 2023/3/5 21:23
 */
@Configuration()
public class LuckPermissionWebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LuckProperties luckProperties;

    static final List<VerifyInterceptor> verifyInterceptors = new LinkedList<>();
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Class[] verifyInterceptors = luckProperties.getVerifyInterceptors();
        System.out.println("正在注册Luck拦截器");
        if (verifyInterceptors.length == 0) return;
        List<VerifyInterceptor> finalVerifyInterceptorList = new LinkedList<>();
        for (Class verifyInterceptor : verifyInterceptors) {
            try {
                finalVerifyInterceptorList.add((VerifyInterceptor) verifyInterceptor.newInstance());
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            registry.addInterceptor(new HandlerInterceptor() {
                List<VerifyInterceptor> verifyInterceptorList = finalVerifyInterceptorList;
                private LuckVerifyEntity getLuckVerifyEntity(HttpServletRequest request){
                    String currentRoute = request.getServletPath();
                    Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                    if (pathVariables!=null)
                        currentRoute = LuckPermission.routePathConver(pathVariables, currentRoute);
                    return LuckPermission.getCurrentLoginVerify(request.getMethod(), currentRoute);

                }
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                    LuckVerifyEntity luckVerifyEntity = getLuckVerifyEntity(request);
                    if (luckVerifyEntity == null) return false;
                    for (VerifyInterceptor verifyInterceptor : verifyInterceptorList) {
                        return verifyInterceptor.preHandle(request, response, handler, luckVerifyEntity);
                    }
                    return HandlerInterceptor.super.preHandle(request, response, handler);
                }

                @Override
                public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
                    HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
                }

                @Override
                public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
                    HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
                }
            }).addPathPatterns(LuckPermission.getRoutes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
