package com.liuscraft.luckpermission.interceptors;

import com.liuscraft.luckpermission.entity.LuckAuthority;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;
import com.liuscraft.luckpermission.service.LuckAuthorityService;
import com.liuscraft.luckpermission.service.LuckGetUserInfoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author LiusCraft
 * @date 2023/3/5 0:40
 */
public class DefaultLuckVerifyInterceptor implements LuckVerifyInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler, LuckVerifyEntity luckVerifyEntity, LuckAuthority luckAuthority) {
        if (!luckVerifyEntity.checkPermissions(luckAuthority.getLuckPermissions())){
            System.out.println("地址:" + request.getRequestURL() +"  需要权限:" + request.getMethod()+":"+ luckVerifyEntity.getRoute());
            try {
                response.getOutputStream().write(("not permission:"+ request.getMethod()+":"+ luckVerifyEntity.getRoute()).getBytes());
                response.getOutputStream().flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return true;
    }
}
