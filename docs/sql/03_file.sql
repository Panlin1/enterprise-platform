-- =============================================================================
-- Phase 13：文件元数据表
-- 库：enterprise_platform
-- 执行：mysql -uroot -p enterprise_platform < docs/sql/03_file.sql
-- =============================================================================

USE enterprise_platform;

DROP TABLE IF EXISTS sys_file;
CREATE TABLE sys_file (
    id              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    original_name   VARCHAR(256)  NOT NULL COMMENT '原始文件名',
    storage_name    VARCHAR(128)  NOT NULL COMMENT '存储文件名（UUID）',
    storage_path    VARCHAR(512)  NOT NULL COMMENT '相对存储路径',
    content_type    VARCHAR(128)  DEFAULT NULL COMMENT 'MIME 类型',
    file_size       BIGINT        NOT NULL DEFAULT 0 COMMENT '字节大小',
    storage_type    VARCHAR(32)   NOT NULL DEFAULT 'LOCAL' COMMENT '存储类型: LOCAL/MINIO/OSS',
    biz_type        VARCHAR(64)   DEFAULT NULL COMMENT '业务类型（可选）',
    uploaded_by     BIGINT        DEFAULT NULL COMMENT '上传人用户ID',
    status          TINYINT       NOT NULL DEFAULT 1 COMMENT '1正常 0禁用',
    remark          VARCHAR(256)  DEFAULT NULL COMMENT '备注',
    created_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除 0否 1是',
    created_by      BIGINT        DEFAULT NULL COMMENT '创建人用户ID',
    updated_by      BIGINT        DEFAULT NULL COMMENT '更新人用户ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_storage_name (storage_name),
    KEY idx_uploaded_by (uploaded_by),
    KEY idx_biz_type (biz_type),
    KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件元数据';
