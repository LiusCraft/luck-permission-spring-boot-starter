package com.liuscraft.luckpermission.interceptors;

import com.liuscraft.luckpermission.entity.LuckVerifyEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LiusCraft
 * @date 2023/3/5 22:47
 */
public interface VerifyInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler, LuckVerifyEntity luckVerifyEntity);
}
