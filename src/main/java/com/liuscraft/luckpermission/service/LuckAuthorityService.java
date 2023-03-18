package com.liuscraft.luckpermission.service;

import com.liuscraft.luckpermission.entity.LuckAuthority;
import org.apache.tomcat.util.http.parser.Authorization;

/**
 * @author LiusCraft
 * @date 2023/3/6 22:47
 */
public interface LuckAuthorityService {
    LuckAuthority getUserAuthorization(Integer userId);
}
