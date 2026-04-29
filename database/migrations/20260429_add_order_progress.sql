-- Add progress records for consult/document orders.
-- Run this once after pulling the order progress feature.

CREATE TABLE IF NOT EXISTS `order_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_type` tinyint NOT NULL COMMENT '订单类型：1-咨询订单，2-文书订单',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `progress` int NOT NULL DEFAULT '0' COMMENT '当前进度百分比',
  `remark` varchar(500) DEFAULT NULL COMMENT '进度说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_type_id` (`order_type`, `order_id`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单进度表';
