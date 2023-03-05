package com.liuscraft.luckpermission.annotations;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LiusCraft
 * @date 2023/3/5 0:43
 */

@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LuckVerify {
    /**
     * 为当前路由以下的所有子路由赋予相同验证要求
      */
    boolean children() default false;
    RequestMethod[] value() default {};
    boolean ignore() default false;
}
