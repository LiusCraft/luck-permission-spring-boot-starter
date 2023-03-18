package com.liuscraft.luckpermission;

import com.liuscraft.luckpermission.annotations.LuckVerifyEnable;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author LiusCraft
 * @date 2023/3/6 18:10
 */
@SpringBootApplication
//@LuckVerifyEnable
@MapperScan(value = "com.liuscraft.luckpermission.mapper")
public class LuckPermissionApplication {
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(LuckPermissionApplication.class, args);
    }
}
