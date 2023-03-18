package com.liuscraft.luckpermission.service.impl;

import com.liuscraft.luckpermission.service.LuckGetUserInfoService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author LiusCraft
 * @date 2023/3/6 23:20
 */
@Service
public class LuckGetUserInfoServiceImpl implements LuckGetUserInfoService {
    @Override
    public Integer getUserId(HttpServletRequest request) {
        return 2;
    }
}
