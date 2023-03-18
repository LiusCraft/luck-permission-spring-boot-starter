package com.liuscraft.luckpermission;

import com.liuscraft.luckpermission.properties.LuckProperties;
import com.liuscraft.luckpermission.service.ILuckPermissionService;
import com.liuscraft.luckpermission.utils.SqlBaseTemplateUtils;
import lombok.extern.log4j.Log4j2;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author LiusCraft
 * @date 2023/3/7 17:56
 */
@Component
@Log4j2
@MapperScan(basePackages = {"com.liuscraft.luckpermission.mapper"})
@ComponentScan(value = {"com.liuscraft.luckpermission"})
public class LuckPermissionEnable {

    private JdbcTemplate jdbcTemplate;
    private LuckProperties luckProperties;
    public LuckPermissionEnable(LuckProperties luckProperties, JdbcTemplate jdbcTemplate, LuckPermissionBuilder luckPermissionBuilder, ILuckPermissionService luckPermissionService) {
        this.jdbcTemplate = jdbcTemplate;
        this.luckProperties = luckProperties;
        initTable();
        if (luckProperties.getRefreshPermission())
        {
            log.info("正在为您进行权限表比对...");
            luckPermissionService.refreshPermissions(luckPermissionBuilder.getRoutesMap());
            log.info("权限表对比完毕");

        }
        else log.warn("本次启动未进行权限表比对，若您的权限注解有更改，请在配置文件中开启刷新权限!");
    }
    private boolean checkTable(String tableName) {
        if (tableName == null || tableName.length()==0) return false;
        String isTableSql = String.format("select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA=database() and TABLE_NAME='%s';", tableName);
        try {
            Map<String, Object> map = jdbcTemplate.queryForMap(isTableSql);
            if(map.isEmpty())return false;
            return tableName.equals(map.get("TABLE_NAME"));
        }catch (Exception e) {
            return false;
        }
    }

    private void initTable() {
        log.info("正在检查您的表信息...");
        String permissionTableName = luckProperties.getTable().getPermissionName();
        String roleTableName = luckProperties.getTable().getRoleName();
        String rolePermissionTableName = luckProperties.getTable().getRolePermissionName();
        if ((!permissionTableName.equals("luck_permission") || !roleTableName.equals("luck_role") || !rolePermissionTableName.equals("luck_role_permission"))&&!LuckPermissionBuilder.useAutoTable)
        {
            log.error("你正在使用自定义表名，需要配置PaginationInterceptor，使用setSqlParserList(LuckPermissionBuilder.getLuckAutoTableHandler())添加动态表名");
            throw new RuntimeException("你正在使用自定义表名，需要配置PaginationInterceptor，使用setSqlParserList(LuckPermissionBuilder.getLuckAutoTableHandler())添加动态表名");
        }

        boolean createNewTable = false;
        if (!checkTable(permissionTableName))
        { // 检查permission表是否存在，不存在则创建
            log.error("配置文件中指定的权限表["+permissionTableName+"]不存在...");
            if (checkTable("luck_permission")){
                permissionTableName = "luck_permission";
                luckProperties.getTable().setPermissionName(permissionTableName);
                log.warn("您数据库中存在luck_permission表，正在使用该表");
            }else {
                log.info("正在为您创建表名["+permissionTableName+"]的权限表");
                jdbcTemplate.execute(SqlBaseTemplateUtils.createLuckPermissionTableSql(permissionTableName));
                jdbcTemplate.execute(SqlBaseTemplateUtils.clearTable(permissionTableName));
                createNewTable = true;
            }
        }

        if (!checkTable(roleTableName))
        { // 检查role表是否存在，不存在则创建

            log.error("配置文件中指定的角色表["+roleTableName+"]不存在...");
            if (checkTable("luck_role")){
                roleTableName = "luck_role";
                luckProperties.getTable().setRoleName(roleTableName);
                log.warn("您数据库中存在luck_role表，正在使用该表");
            }else {
                log.info("正在为您创建表名["+roleTableName+"]的角色表");
                jdbcTemplate.execute(SqlBaseTemplateUtils.createLuckRoleTableSql(roleTableName));
                jdbcTemplate.execute(SqlBaseTemplateUtils.clearTable(roleTableName));
                createNewTable = true;
            }
        }

        if (!checkTable(rolePermissionTableName))
        { // 检查permission表是否存在，不存在则创建

            log.error("配置文件中指定的权限与角色关联表["+rolePermissionTableName+"]不存在...");
            if (checkTable("luck_role_permission")){
                rolePermissionTableName = "luck_role_permission";
                log.warn("您数据库中存在luck_role_permission表，正在使用该表");
                luckProperties.getTable().setRolePermissionName(rolePermissionTableName);
            }else {
                log.info("正在为您创建表名["+rolePermissionTableName+"]的权限与角色关联表");
                jdbcTemplate.execute(SqlBaseTemplateUtils.createLuckRolePermissionTableSql(rolePermissionTableName, permissionTableName, roleTableName));
                jdbcTemplate.execute(SqlBaseTemplateUtils.clearTable(rolePermissionTableName));
            }
        }else if (createNewTable) {
            log.error("由于您的权限表或角色表重新创建了，请检查"+rolePermissionTableName+"表的外键是否需要更改！");
        }
        log.info("表信息检查完毕");

    }

}
