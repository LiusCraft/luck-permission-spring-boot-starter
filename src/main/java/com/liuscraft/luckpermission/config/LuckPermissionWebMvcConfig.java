package com.liuscraft.luckpermission.config;

import com.liuscraft.luckpermission.LuckPermissionBuilder;
import com.liuscraft.luckpermission.entity.LuckAuthority;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;
import com.liuscraft.luckpermission.interceptors.LuckVerifyInterceptor;
import com.liuscraft.luckpermission.properties.LuckProperties;
import com.liuscraft.luckpermission.service.LuckAuthorityService;
import com.liuscraft.luckpermission.service.LuckGetUserInfoService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author LiusCraft
 * @date 2023/3/5 21:23
 */
@Configuration
public class LuckPermissionWebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LuckPermissionBuilder luckPermissionBuilder;

    @Resource
    LuckAuthorityService luckAuthorityService;
    @Resource
    LuckGetUserInfoService luckGetUserInfoService;
    @Resource
    LuckProperties luckProperties;

    static final List<LuckVerifyInterceptor> LUCK_VERIFY_INTERCEPTORS = new LinkedList<>();
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (luckPermissionBuilder.getRoutePaths().size()==0) return;
        System.out.println("[LuckPermission] 正在注册Luck拦截器");
        Class[] verifyInterceptors = luckProperties.getVerifyInterceptors();
        if (verifyInterceptors.length == 0) return;
        List<LuckVerifyInterceptor> finalLuckVerifyInterceptorList = new LinkedList<>();
        for (Class verifyInterceptor : verifyInterceptors) {
            System.out.println("正在拦截器:"+verifyInterceptor.getName());
            try {
                finalLuckVerifyInterceptorList.add((LuckVerifyInterceptor) verifyInterceptor.newInstance());
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            registry.addInterceptor(new HandlerInterceptor() {
                List<LuckVerifyInterceptor> luckVerifyInterceptorList = finalLuckVerifyInterceptorList;
                private LuckVerifyEntity getLuckVerifyEntity(HttpServletRequest request){
                    String currentRoute = request.getServletPath();
                    Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
                    if (pathVariables!=null)
                        currentRoute = luckPermissionBuilder.routePathConver(pathVariables, currentRoute);
                    return luckPermissionBuilder.getCurrentLoginVerify(request.getMethod(), currentRoute);

                }
                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                    LuckVerifyEntity luckVerifyEntity = getLuckVerifyEntity(request);
                    if (luckVerifyEntity == null) return false;
                    if (luckVerifyEntity.getIgnore()) return true;
                    LuckAuthority userAuthorization = luckAuthorityService.getUserAuthorization(luckGetUserInfoService.getUserId(request));
                    if (userAuthorization == null) {
                        System.out.println("无法正确获取到userAuthorization！");
                    }
                    for (LuckVerifyInterceptor luckVerifyInterceptor : luckVerifyInterceptorList) {
                        if(!luckVerifyInterceptor.preHandle(request, response, handler, luckVerifyEntity, userAuthorization))
                            return false;
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
            }).addPathPatterns(new ArrayList<>(luckPermissionBuilder.getRoutePaths()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
