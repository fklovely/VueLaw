package com.legal.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderProgressSchemaInitializer implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS `order_progress` ("
                            + "`id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',"
                            + "`order_type` tinyint NOT NULL COMMENT '订单类型：1-咨询订单，2-文书订单',"
                            + "`order_id` bigint NOT NULL COMMENT '订单ID',"
                            + "`progress` int NOT NULL DEFAULT '0' COMMENT '当前进度百分比',"
                            + "`remark` varchar(500) DEFAULT NULL COMMENT '进度说明',"
                            + "`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',"
                            + "`update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',"
                            + "PRIMARY KEY (`id`),"
                            + "UNIQUE KEY `uk_order_type_id` (`order_type`, `order_id`),"
                            + "KEY `idx_update_time` (`update_time`)"
                            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单进度表'"
            );
        } catch (Exception e) {
            System.err.println("订单进度表自动初始化失败，请手动执行 database/migrations/20260429_add_order_progress.sql: " + e.getMessage());
        }
    }
}
