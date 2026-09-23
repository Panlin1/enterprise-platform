-- =============================================================================
-- enterprise-platform 数据库结构
-- 字符集: utf8mb4 / utf8mb4_unicode_ci
-- 引擎: InnoDB
-- 规范: 表名 snake_case, 主键 bigint, 时间 datetime, 逻辑删除 deleted
-- =============================================================================

CREATE DATABASE IF NOT EXISTS enterprise_platform
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE enterprise_platform;

-- -----------------------------------------------------------------------------
-- 1. sys_user 用户表
-- 用途: 登录账号、基本资料、状态
-- 唯一: username, phone(可空唯一), email(可空唯一)
-- 逻辑删除: deleted
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
                          id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                          username        VARCHAR(64)  NOT NULL COMMENT '登录用户名',
                          password        VARCHAR(128) NOT NULL COMMENT 'BCrypt 密码密文',
                          nickname        VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
                          real_name       VARCHAR(64)  DEFAULT NULL COMMENT '真实姓名',
                          email           VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
                          phone           VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
                          avatar          VARCHAR(512) DEFAULT NULL COMMENT '头像 URL',
                          gender          TINYINT      DEFAULT 0 COMMENT '性别: 0未知 1男 2女',
                          status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
                          remark          VARCHAR(512) DEFAULT NULL COMMENT '备注',
                          last_login_at   DATETIME     DEFAULT NULL COMMENT '最后登录时间',
                          last_login_ip   VARCHAR(64)  DEFAULT NULL COMMENT '最后登录 IP',
                          created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          created_by      BIGINT       DEFAULT NULL COMMENT '创建人',
                          updated_by      BIGINT       DEFAULT NULL COMMENT '更新人',
                          deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_username (username),
                          UNIQUE KEY uk_phone (phone),
                          UNIQUE KEY uk_email (email),
                          KEY idx_status (status),
                          KEY idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户';

-- -----------------------------------------------------------------------------
-- 2. sys_role 角色表
-- 用途: RBAC 角色（ADMIN / USER 等）
-- 唯一: role_code
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
                          id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                          role_code       VARCHAR(64)  NOT NULL COMMENT '角色编码，如 ADMIN',
                          role_name       VARCHAR(64)  NOT NULL COMMENT '角色名称',
                          sort            INT          NOT NULL DEFAULT 0 COMMENT '排序（越小越靠前）',
                          status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
                          remark          VARCHAR(512) DEFAULT NULL COMMENT '备注',
                          created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          created_by      BIGINT       DEFAULT NULL COMMENT '创建人',
                          updated_by      BIGINT       DEFAULT NULL COMMENT '更新人',
                          deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_role_code (role_code),
                          KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统角色';

-- -----------------------------------------------------------------------------
-- 3. sys_permission 权限表
-- 用途: 接口/按钮权限标识，与 @SaCheckPermission 字符串一致
-- 示例: system:user:list
-- 唯一: permission_code
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
                                id               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                                permission_code  VARCHAR(128) NOT NULL COMMENT '权限标识，如 system:user:list',
                                permission_name  VARCHAR(64)  NOT NULL COMMENT '权限名称',
                                permission_type  TINYINT      NOT NULL DEFAULT 1 COMMENT '类型: 1菜单 2按钮 3接口',
                                parent_id        BIGINT       NOT NULL DEFAULT 0 COMMENT '父权限 ID，0 为顶级',
                                path             VARCHAR(256) DEFAULT NULL COMMENT '路由/资源路径',
                                method           VARCHAR(16)  DEFAULT NULL COMMENT 'HTTP 方法，接口权限用',
                                sort             INT          NOT NULL DEFAULT 0 COMMENT '排序',
                                status           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
                                remark           VARCHAR(512) DEFAULT NULL COMMENT '备注',
                                created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                updated_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                created_by       BIGINT       DEFAULT NULL COMMENT '创建人',
                                updated_by       BIGINT       DEFAULT NULL COMMENT '更新人',
                                deleted          TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
                                PRIMARY KEY (id),
                                UNIQUE KEY uk_permission_code (permission_code),
                                KEY idx_parent_id (parent_id),
                                KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统权限';

-- -----------------------------------------------------------------------------
-- 4. sys_menu 菜单表
-- 用途: 前端侧栏/路由菜单（可与权限关联）
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
                          id              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                          parent_id       BIGINT       NOT NULL DEFAULT 0 COMMENT '父菜单 ID，0 为顶级',
                          menu_name       VARCHAR(64)  NOT NULL COMMENT '菜单名称',
                          menu_type       TINYINT      NOT NULL DEFAULT 1 COMMENT '类型: 1目录 2菜单 3按钮',
                          path            VARCHAR(256) DEFAULT NULL COMMENT '路由 path',
                          component       VARCHAR(256) DEFAULT NULL COMMENT '前端组件路径',
                          permission_code VARCHAR(128) DEFAULT NULL COMMENT '关联权限标识',
                          icon            VARCHAR(64)  DEFAULT NULL COMMENT '图标',
                          sort            INT          NOT NULL DEFAULT 0 COMMENT '排序',
                          visible         TINYINT      NOT NULL DEFAULT 1 COMMENT '是否可见: 0隐藏 1显示',
                          status          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
                          remark          VARCHAR(512) DEFAULT NULL COMMENT '备注',
                          created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          created_by      BIGINT       DEFAULT NULL COMMENT '创建人',
                          updated_by      BIGINT       DEFAULT NULL COMMENT '更新人',
                          deleted         TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
                          PRIMARY KEY (id),
                          KEY idx_parent_id (parent_id),
                          KEY idx_permission_code (permission_code),
                          KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统菜单';

-- -----------------------------------------------------------------------------
-- 5. sys_user_role 用户-角色关联
-- 唯一: (user_id, role_id)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
                               id         BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
                               user_id    BIGINT   NOT NULL COMMENT '用户 ID',
                               role_id    BIGINT   NOT NULL COMMENT '角色 ID',
                               created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               PRIMARY KEY (id),
                               UNIQUE KEY uk_user_role (user_id, role_id),
                               KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联';

-- -----------------------------------------------------------------------------
-- 6. sys_role_permission 角色-权限关联
-- 唯一: (role_id, permission_id)
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
                                     id            BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     role_id       BIGINT   NOT NULL COMMENT '角色 ID',
                                     permission_id BIGINT   NOT NULL COMMENT '权限 ID',
                                     created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                     PRIMARY KEY (id),
                                     UNIQUE KEY uk_role_permission (role_id, permission_id),
                                     KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联';

-- -----------------------------------------------------------------------------
-- 7. sys_dict 字典类型
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_dict;
CREATE TABLE sys_dict (
                          id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                          dict_type   VARCHAR(64)  NOT NULL COMMENT '字典类型编码',
                          dict_name   VARCHAR(64)  NOT NULL COMMENT '字典名称',
                          status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
                          remark      VARCHAR(512) DEFAULT NULL COMMENT '备注',
                          created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          created_by  BIGINT       DEFAULT NULL COMMENT '创建人',
                          updated_by  BIGINT       DEFAULT NULL COMMENT '更新人',
                          deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典类型';

-- -----------------------------------------------------------------------------
-- 8. sys_dict_item 字典项
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_dict_item;
CREATE TABLE sys_dict_item (
                               id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                               dict_type   VARCHAR(64)  NOT NULL COMMENT '字典类型编码',
                               item_label  VARCHAR(64)  NOT NULL COMMENT '显示标签',
                               item_value  VARCHAR(64)  NOT NULL COMMENT '存储值',
                               sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
                               status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
                               remark      VARCHAR(512) DEFAULT NULL COMMENT '备注',
                               created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                               updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                               created_by  BIGINT       DEFAULT NULL COMMENT '创建人',
                               updated_by  BIGINT       DEFAULT NULL COMMENT '更新人',
                               deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
                               PRIMARY KEY (id),
                               UNIQUE KEY uk_dict_type_value (dict_type, item_value),
                               KEY idx_dict_type (dict_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典项';

-- -----------------------------------------------------------------------------
-- 9. sys_config 系统参数
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
                            id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                            config_key   VARCHAR(128) NOT NULL COMMENT '参数键',
                            config_value VARCHAR(1024) NOT NULL COMMENT '参数值',
                            config_name  VARCHAR(64)  NOT NULL COMMENT '参数名称',
                            config_type  TINYINT      NOT NULL DEFAULT 0 COMMENT '类型: 0系统内置 1业务自定义',
                            remark       VARCHAR(512) DEFAULT NULL COMMENT '备注',
                            created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            created_by   BIGINT       DEFAULT NULL COMMENT '创建人',
                            updated_by   BIGINT       DEFAULT NULL COMMENT '更新人',
                            deleted      TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0正常 1删除',
                            PRIMARY KEY (id),
                            UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统参数配置';

-- -----------------------------------------------------------------------------
-- 10. sys_login_log 登录日志（建议物理删除或按时间归档，仍保留 deleted 字段可选）
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_login_log;
CREATE TABLE sys_login_log (
                               id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                               user_id       BIGINT       DEFAULT NULL COMMENT '用户 ID',
                               username      VARCHAR(64)  DEFAULT NULL COMMENT '登录用户名',
                               login_type    TINYINT      NOT NULL DEFAULT 1 COMMENT '登录类型: 1账号密码 2其他',
                               status        TINYINT      NOT NULL COMMENT '结果: 0失败 1成功',
                               ip            VARCHAR(64)  DEFAULT NULL COMMENT 'IP',
                               user_agent    VARCHAR(512) DEFAULT NULL COMMENT 'User-Agent',
                               message       VARCHAR(256) DEFAULT NULL COMMENT '消息/失败原因',
                               login_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
                               PRIMARY KEY (id),
                               KEY idx_user_id (user_id),
                               KEY idx_username (username),
                               KEY idx_login_at (login_at),
                               KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='登录日志';

-- -----------------------------------------------------------------------------
-- 11. sys_operation_log 操作日志
-- -----------------------------------------------------------------------------
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
                                   id             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
                                   user_id        BIGINT       DEFAULT NULL COMMENT '操作人 ID',
                                   username       VARCHAR(64)  DEFAULT NULL COMMENT '操作人用户名',
                                   module         VARCHAR(64)  DEFAULT NULL COMMENT '模块',
                                   operation      VARCHAR(64)  DEFAULT NULL COMMENT '操作类型',
                                   method         VARCHAR(16)  DEFAULT NULL COMMENT 'HTTP 方法',
                                   request_uri    VARCHAR(512) DEFAULT NULL COMMENT '请求 URI',
                                   request_id     VARCHAR(64)  DEFAULT NULL COMMENT '请求 ID',
                                   request_params TEXT         DEFAULT NULL COMMENT '请求参数（脱敏后）',
                                   ip             VARCHAR(64)  DEFAULT NULL COMMENT 'IP',
                                   status         TINYINT      NOT NULL DEFAULT 1 COMMENT '结果: 0失败 1成功',
                                   error_msg      VARCHAR(1024) DEFAULT NULL COMMENT '错误信息',
                                   duration_ms    INT          DEFAULT NULL COMMENT '耗时毫秒',
                                   operated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
                                   PRIMARY KEY (id),
                                   KEY idx_user_id (user_id),
                                   KEY idx_operated_at (operated_at),
                                   KEY idx_module (module),
                                   KEY idx_request_id (request_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志';
