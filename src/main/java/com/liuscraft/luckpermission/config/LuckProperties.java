package com.liuscraft.luckpermission.config;

import com.liuscraft.luckpermission.LuckPermission;
import com.liuscraft.luckpermission.interceptors.DefaultVerifyInterceptor;
import com.liuscraft.luckpermission.interceptors.VerifyInterceptor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author LiusCraft
 * @date 2023/3/5 21:01
 */
@Component
@ConfigurationProperties(
        prefix = "luck-permission",
        ignoreUnknownFields = true
)
@Getter
@Setter
public class LuckProperties {
    private String[] packages;
    private Class[] verifyInterceptors = {DefaultVerifyInterceptor.class};

    public void setPackages(String[] packages) {
        this.packages = packages;
        if (packages == null || packages.length==0) throw new RuntimeException("请指定controller包路径");
        for (String aPackage : packages) {
            try {
                LuckPermission.addPackVerifyAnnotation(aPackage);
            } catch (Exception e) {
                throw new RuntimeException("在加载 "+ aPackage+" 时发生了错误...");
            }
        }
    }
}
