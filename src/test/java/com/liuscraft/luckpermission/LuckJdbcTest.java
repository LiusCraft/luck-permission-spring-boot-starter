package com.liuscraft.luckpermission;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author LiusCraft
 * @date 2023/3/7 13:43
 */
@SpringBootTest
public class LuckJdbcTest {
    @Resource
    JdbcTemplate jdbcTemplate;
    @Test
    public void testjdbc() {
        String isTableSql = String.format("SELECT COUNT(*) as count FROM information_schema.TABLES WHERE " + "table_name = '%s'", "luck_spermission");
        Map<String, Object> map = jdbcTemplate.queryForMap(isTableSql);
        System.out.println(map.get("count").toString());
    }


}
