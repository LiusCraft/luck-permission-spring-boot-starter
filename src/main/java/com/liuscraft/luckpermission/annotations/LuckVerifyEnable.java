package com.liuscraft.luckpermission.annotations;

import com.liuscraft.luckpermission.LuckPermissionEnable;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * @author LiusCraft
 * @date 2023/3/5 22:03
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LuckPermissionEnable.class)
public @interface LuckVerifyEnable {
}
