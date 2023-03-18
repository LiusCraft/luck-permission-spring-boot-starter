package com.liuscraft.luckpermission.controller;


import com.liuscraft.luckpermission.annotations.LuckVerify;
import com.liuscraft.luckpermission.utils.LuckR;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author LiusCraft
 * @since 2023-03-06
 */
@RestController
@LuckVerify()
@RequestMapping("/luck-role-permissions")
public class LuckRolePermissionController {

    @GetMapping
    public LuckR get() {
        return LuckR.ok();
    }

}

