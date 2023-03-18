package com.liuscraft.luckpermission.service;

import com.liuscraft.luckpermission.entity.LuckRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscraft.luckpermission.entity.LuckVerifyEntity;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LiusCraft
 * @since 2023-03-06
 */
public interface ILuckRoleService extends IService<LuckRole> {

    LuckRole getRoleByName(String name);

    LuckRole getRoleById(Integer id);

    void initSuperRole(Map<String, LuckVerifyEntity> luckVerifyEntityMap);

}
