package com.liuscraft.luckpermission.entity;

import java.util.List;

/**
 * @author LiusCraft
 * @date 2023/3/6 22:48
 */
public class LuckAuthority {
    Object data;
    LuckRole luckRole;
    List<LuckPermission> luckPermissions;

    public LuckAuthority(LuckRole luckRole, List<LuckPermission> luckPermissions) {
        this.luckRole = luckRole;
        this.luckPermissions = luckPermissions;
    }

    public LuckRole getLuckRole() {
        return luckRole;
    }

    public List<LuckPermission> getLuckPermissions() {
        return luckPermissions;
    }


    /**
     * 可放个对象到LuckAuthority
     * @param user
     */
    public void setData(Object user) {
        this.data = user;
    }

    public <T> T getData(Class<T> tClass) {
        T cast = null;
        try {
            cast = tClass.cast(this.data);
        }catch (Exception e) {
            throw new ClassCastException("luckPermission 无法正常获取data");
        }
        return cast;
    }
}
