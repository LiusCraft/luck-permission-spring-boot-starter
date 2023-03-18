package com.liuscraft.luckpermission.utils;

/**
 * @author LiusCraft
 * @date 2023/3/7 15:38
 */
public class SqlBaseTemplateUtils {

    public static String clearTable(String tableName) {
        return "truncate table `"+tableName+"`;";
    }
    public static String createLuckPermissionTableSql(String tableName) {
        return "CREATE TABLE `"+tableName+"` (\n" +
                "  `id` int NOT NULL AUTO_INCREMENT,\n" +
                "  `method` varchar(10) COLLATE utf8mb4_general_ci NOT NULL,\n" +
                "  `route` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,\n" +
                "  `description` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '无描述',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `"+tableName+"_un_m_r` (`method`,`route`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
    }

    public static String createLuckRoleTableSql(String tableName) {
        return "CREATE TABLE `"+tableName+"` (\n" +
                "  `id` int NOT NULL AUTO_INCREMENT,\n" +
                "  `role_name` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,\n" +
                "  `role_description` varchar(200) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '该角色无描述',\n" +
                "  `role_ban` tinyint NOT NULL DEFAULT '0',\n" +
                "  PRIMARY KEY (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
    }

    public static String createLuckRolePermissionTableSql(String tableName, String permissionName, String roleName) {
        return "CREATE TABLE `"+tableName+"` (\n" +
                "  `role_id` int NOT NULL,\n" +
                "  `permission_id` int NOT NULL,\n" +
                "  `allow` tinyint NOT NULL DEFAULT 1,\n" +
                "  KEY `"+tableName+"_FK_1` (`permission_id`),\n" +
                "  KEY `"+tableName+"_role_id_IDX` (`role_id`,`permission_id`,`allow`) USING BTREE,\n" +
                "  CONSTRAINT `"+tableName+"_FK` FOREIGN KEY (`role_id`) REFERENCES `"+roleName+"` (`id`) ON DELETE CASCADE,\n" +
                "  CONSTRAINT `"+tableName+"_FK_1` FOREIGN KEY (`permission_id`) REFERENCES `"+permissionName+"` (`id`) ON DELETE CASCADE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
    }
}
