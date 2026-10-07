-- =============================================================================
-- Phase 14：业务示例表（工单）
-- 执行：mysql -uroot -p enterprise_platform < docs/sql/04_business.sql
-- =============================================================================

USE enterprise_platform;

DROP TABLE IF EXISTS biz_work_order;
CREATE TABLE biz_work_order (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no        VARCHAR(32)   NOT NULL COMMENT '工单编号',
    title           VARCHAR(128)  NOT NULL COMMENT '标题',
    content         VARCHAR(1024) DEFAULT NULL COMMENT '内容描述',
    priority        TINYINT       NOT NULL DEFAULT 2 COMMENT '优先级: 1高 2中 3低',
    status          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0待处理 1处理中 2已完成 3已关闭',
    assignee_id     BIGINT        DEFAULT NULL COMMENT '处理人用户ID',
    creator_id      BIGINT        DEFAULT NULL COMMENT '创建人用户ID',
    remark          VARCHAR(256)  DEFAULT NULL COMMENT '备注',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_status (status),
    KEY idx_creator_id (creator_id),
    KEY idx_assignee_id (assignee_id),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='业务工单（示例）';

ALTER TABLE biz_work_order
    ADD COLUMN created_by BIGINT DEFAULT NULL COMMENT '创建人用户ID' AFTER updated_at,
    ADD COLUMN updated_by BIGINT DEFAULT NULL COMMENT '更新人用户ID' AFTER created_by;
