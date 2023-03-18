package com.liuscraft.luckpermission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuscraft.luckpermission.entity.LuckPermission;
import com.liuscraft.luckpermission.entity.LuckRole;
import com.liuscraft.luckpermission.entity.LuckRolePermission;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;
import com.liuscraft.luckpermission.mapper.LuckRoleMapper;
import com.liuscraft.luckpermission.properties.LuckProperties;
import com.liuscraft.luckpermission.service.ILuckPermissionService;
import com.liuscraft.luckpermission.service.ILuckRolePermissionService;
import com.liuscraft.luckpermission.service.ILuckRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LiusCraft
 * @since 2023-03-06
 */
@Service
public class LuckRoleServiceImpl extends ServiceImpl<LuckRoleMapper, LuckRole> implements ILuckRoleService {

    @Resource
    ILuckPermissionService luckPermissionService;

    @Resource
    ILuckRolePermissionService luckRolePermissionService;


    @Override
    public LuckRole getRoleByName(String name) {
        QueryWrapper<LuckRole> luckRoleQueryWrapper = new QueryWrapper<>();
        luckRoleQueryWrapper.eq("role_name", name);
        return getBaseMapper().selectOne(luckRoleQueryWrapper);
    }

    @Override
    public LuckRole getRoleById(Integer id) {
        return getBaseMapper().selectById(id);
    }

    @Override
    public void initSuperRole(Map<String, LuckVerifyEntity> luckVerifyEntityMap) {
        LuckRole roleByName = getRoleByName(LuckProperties.getLuckProperties().getSuperAdminName());
        if (roleByName == null){
            roleByName = new LuckRole();
            roleByName.setRoleName(LuckProperties.getLuckProperties().getSuperAdminName());
            roleByName.setRoleDescription("超级管理员，可访问luckPermissionAPI");
            getBaseMapper().insert(roleByName);
        }


        List<Integer> perIds = new LinkedList<>();
        luckVerifyEntityMap.values().forEach(v->{
            LuckPermission byMethodAndRoute = luckPermissionService.getByMethodAndRoute(v.getRequestMethod(), v.getRoute());
            if (byMethodAndRoute!= null) perIds.add(byMethodAndRoute.getId());
        });

        LuckRole finalRoleByName = roleByName;
        perIds.forEach(v->{
            luckRolePermissionService.addPermissionToRole(v, finalRoleByName.getId());
        });
    }
}
