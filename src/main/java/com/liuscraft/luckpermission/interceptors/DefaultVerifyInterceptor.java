package com.liuscraft.luckpermission.interceptors;

import com.liuscraft.luckpermission.LuckPermission;
import com.liuscraft.luckpermission.annotations.LuckVerify;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;
import com.liuscraft.luckpermission.utils.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author LiusCraft
 * @date 2023/3/5 0:40
 */
public class DefaultVerifyInterceptor implements VerifyInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler, LuckVerifyEntity luckVerifyEntity) {
        System.out.println("地址:" + request.getServletPath() +"  需要权限:" + request.getMethod()+":"+ luckVerifyEntity.getRoute());
        return false;
    }
}
