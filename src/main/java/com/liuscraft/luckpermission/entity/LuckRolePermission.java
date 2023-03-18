package com.liuscraft.luckpermission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author LiusCraft
 * @since 2023-03-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("luck_role_permission")
public class LuckRolePermission implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer roleId;

    private Integer permissionId;

    private Integer allow;


}
