package com.liuscraft.luckpermission.service.impl;

import com.liuscraft.luckpermission.entity.LuckPermission;
import com.liuscraft.luckpermission.entity.LuckRolePermission;
import com.liuscraft.luckpermission.mapper.LuckRolePermissionMapper;
import com.liuscraft.luckpermission.service.ILuckRolePermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LiusCraft
 * @since 2023-03-06
 */
@Service
public class LuckRolePermissionServiceImpl extends ServiceImpl<LuckRolePermissionMapper, LuckRolePermission> implements ILuckRolePermissionService {

    @Override
    public LuckRolePermission addPermissionToRole(Integer permissionId, Integer roleId) {
        LuckRolePermission luckRolePermission = new LuckRolePermission();
        luckRolePermission.setPermissionId(permissionId);
        luckRolePermission.setRoleId(roleId);
        getBaseMapper().insert(luckRolePermission);
        return luckRolePermission;
    }
}
