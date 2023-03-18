package com.liuscraft.luckpermission.service;

import com.liuscraft.luckpermission.entity.LuckPermission;
import com.liuscraft.luckpermission.entity.LuckRolePermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LiusCraft
 * @since 2023-03-06
 */
public interface ILuckRolePermissionService extends IService<LuckRolePermission> {

    LuckRolePermission addPermissionToRole(Integer permissionId, Integer roleId);

}
