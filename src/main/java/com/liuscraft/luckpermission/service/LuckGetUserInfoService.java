package com.liuscraft.luckpermission.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author LiusCraft
 * @date 2023/3/6 23:19
 */
public interface LuckGetUserInfoService {
    Integer getUserId(HttpServletRequest request);
}
