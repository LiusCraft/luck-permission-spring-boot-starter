package com.liuscraft.luckpermission.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@TableName("luck_permission")
@AllArgsConstructor
public class LuckPermission implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String method;

    private String route;

    private String description;

    public LuckPermission(String method, String route, String description) {
        this.method = method;
        this.route = route;
        this.description = description;
    }

    public String getMethodRoute() {
        return this.method+":"+this.route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        return getMethodRoute().equalsIgnoreCase(o.toString());
    }

    @Override
    public String toString() {
        return getMethodRoute();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethod(), getRoute());
    }

}
