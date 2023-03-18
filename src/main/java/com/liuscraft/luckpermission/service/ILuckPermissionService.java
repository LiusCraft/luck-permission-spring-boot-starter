package com.liuscraft.luckpermission.service;

import com.liuscraft.luckpermission.entity.LuckPermission;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;

import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LiusCraft
 * @since 2023-03-06
 */
public interface ILuckPermissionService extends IService<LuckPermission> {
    /**
     * 查询该权限是否存在
     * @param luckVerifyEntity 权限entity
     * @return 查询到该权限
     */
    Boolean checkPermission(LuckVerifyEntity luckVerifyEntity);

    /**
     * 查询该权限是否存在
     * @param luckVerifyEntity 权限entity
     * @param create 若未查询到则为表中加入该权限
     * @return 查询到或加入成功
     */
    Boolean checkPermission(LuckVerifyEntity luckVerifyEntity, Boolean create);

    Boolean refreshPermissions(Map<String, LuckVerifyEntity> luckVerifyEntities);

    LuckPermission getByMethodAndRoute(String method, String route);

}
