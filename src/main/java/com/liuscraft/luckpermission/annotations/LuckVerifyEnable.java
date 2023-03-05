package com.liuscraft.luckpermission.annotations;

import com.liuscraft.luckpermission.LuckPermission;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author LiusCraft
 * @date 2023/3/5 22:03
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LuckPermission.class)
public @interface LuckVerifyEnable {
}
