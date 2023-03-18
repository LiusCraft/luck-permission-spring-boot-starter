package com.liuscraft.luckpermission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuscraft.luckpermission.entity.LuckAuthority;
import com.liuscraft.luckpermission.entity.LuckPermission;
import com.liuscraft.luckpermission.entity.LuckRole;
import com.liuscraft.luckpermission.entity.LuckRolePermission;
import com.liuscraft.luckpermission.service.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LiusCraft
 * @date 2023/3/6 22:56
 */
@Service
public class LuckAuthorityServiceImpl implements LuckAuthorityService {

    @Resource
    ILuckRoleService luckRoleService;
    @Resource
    ILuckRolePermissionService luckRolePermissionService;
    @Resource
    ILuckPermissionService luckPermissionService;
    @Override
    public LuckAuthority getUserAuthorization(Integer roleId) {
        if (roleId==null || roleId.intValue()<1) return null;
        LuckRole luckRole = luckRoleService.getBaseMapper().selectById(roleId);
        LuckAuthority authorization = null;
        if (luckRole == null) {
            return null;
        }

        QueryWrapper<LuckRolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", luckRole.getId());
        List<LuckRolePermission> luckRolePermissions = luckRolePermissionService.getBaseMapper().selectList(queryWrapper);
        if (luckRolePermissions == null) luckRolePermissions = new LinkedList<>();
        Set<Integer> permissionIds = luckRolePermissions.stream().map(v -> v.getPermissionId()).collect(Collectors.toSet());
        List<LuckPermission> luckPermissions = new LinkedList<>();
        if (!permissionIds.isEmpty())
            luckPermissions = luckPermissionService.getBaseMapper().selectBatchIds(permissionIds);
        authorization = new LuckAuthority(luckRole, luckPermissions);
        return authorization;
    }
}
