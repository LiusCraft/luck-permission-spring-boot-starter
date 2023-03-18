package com.liuscraft.luckpermission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liuscraft.luckpermission.entity.LuckPermission;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;
import com.liuscraft.luckpermission.mapper.LuckPermissionMapper;
import com.liuscraft.luckpermission.service.ILuckPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author LiusCraft
 * @since 2023-03-06
 */
@Service
@Log4j2
public class LuckPermissionServiceImpl extends ServiceImpl<LuckPermissionMapper, LuckPermission> implements ILuckPermissionService {

    @Override
    public Boolean checkPermission(LuckVerifyEntity luckVerifyEntity) {
        if (luckVerifyEntity == null) return false;
        QueryWrapper<LuckPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("method", luckVerifyEntity.getRequestMethod()).eq("route", luckVerifyEntity.getRoute());
        LuckPermission luckPermission = getBaseMapper().selectOne(queryWrapper);
        return luckPermission!=null;
    }

    @Override
    public Boolean checkPermission(LuckVerifyEntity luckVerifyEntity, Boolean create) {
        if(!checkPermission(luckVerifyEntity)){
            // permission表中无该methodRoute权限节点，判断是否可创建
            if (!create) {
                return false;// create false 不创建
            }
        }
        return null;
    }



    @Override
    public Boolean refreshPermissions(Map<String, LuckVerifyEntity> luckVerifyEntities) {
        List<LuckPermission> luckPermissions = getBaseMapper().selectList(null);
        Set<LuckVerifyEntity> noIgnoreVerifyEntitys = luckVerifyEntities.values().stream()
                .filter(v -> !v.getIgnore()).collect(Collectors.toSet());
        if (luckPermissions == null|| luckPermissions.size() == 0) {
            log.info("发现您的权限表中没有任何权限信息，正在将当前项目的所有权限节点配置到权限表中...");
            noIgnoreVerifyEntitys.stream()
                    .map(v-> new LuckPermission(v.getRequestMethod(), v.getRoute(), v.getDescription()))
                    .collect(Collectors.toSet())
                    .forEach(v->{
                        getBaseMapper().insert(v);
                    });
            return true;
        }
        List<Integer> removeIds = new LinkedList<>();
        Set<LuckVerifyEntity> unAddLuckVerifyEntity = new HashSet<>(luckVerifyEntities.values());
        luckPermissions.forEach(v->{
            unAddLuckVerifyEntity.remove(v);
            LuckVerifyEntity luckVerifyEntity = luckVerifyEntities.get(v.getMethodRoute());
            if (luckVerifyEntity == null || luckVerifyEntity.getIgnore()) {
                removeIds.add(v.getId());
                log.info("表中权限: "+ v.getMethodRoute() +"因已在项目中删除，所以表中已删除");
            }else if (!v.getDescription().equals(luckVerifyEntity.getDescription())){
                v.setDescription(luckVerifyEntity.getDescription());
                getBaseMapper().updateById(v);
                log.info("表中权限: "+ v.getMethodRoute() +"的描述更新为"+luckVerifyEntity.getDescription());
            }
        });
        unAddLuckVerifyEntity.stream().filter(v->!v.getIgnore()).map(v-> new LuckPermission(v.getRequestMethod(), v.getRoute(), v.getDescription()))
                .collect(Collectors.toSet())
                .forEach(v->{
                    getBaseMapper().insert(v);
                    log.info("新增权限: "+ v.getMethodRoute() +"到表中");
                });
        if (!removeIds.isEmpty())
            getBaseMapper().deleteBatchIds(removeIds);
        return true;
    }

    @Override
    public LuckPermission getByMethodAndRoute(String method, String route) {
        QueryWrapper<LuckPermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("method", method).eq("route",route);
        LuckPermission luckPermission = getBaseMapper().selectOne(queryWrapper);
        return luckPermission;
    }
}
