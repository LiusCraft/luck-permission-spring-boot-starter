package com.liuscraft.luckpermission.properties;

import com.liuscraft.luckpermission.interceptors.DefaultLuckVerifyInterceptor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author LiusCraft
 * @date 2023/3/5 21:01
 */
/*@Component
@ConfigurationProperties(
        prefix = "luck-permission",
        ignoreUnknownFields = true
)*/
    @Component
@ConfigurationProperties(
        prefix = "luck-permission"
)
@Getter
@Setter
public class LuckProperties {
    private static LuckProperties luckProperties;

    public static LuckProperties getLuckProperties() {
        if (luckProperties == null) luckProperties = new LuckProperties();
        return luckProperties;
    }

    public LuckProperties() {
        luckProperties = this;
    }
    private String superAdminName = "SuperAdmin";
    private String[] packages = {"com.liuscraft.luckpermission.controller"};
    private Class[] verifyInterceptors = {DefaultLuckVerifyInterceptor.class};
    private Boolean refreshPermission = true;
    private final Table table = new Table();
    @Data
    public static class Table {
        private String permissionName = "luck_permission";
        private String roleName = "luck_role";
        private String rolePermissionName = "luck_role_permission";
    }

    public void setPackages(String[] packages) {
        this.packages = packages;
        if (packages == null || packages.length==0) throw new RuntimeException("请指定controller包路径");
    }
}
