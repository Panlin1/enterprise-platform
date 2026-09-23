# 数据库设计说明

> 本文档与 `docs/sql/01_schema.sql`（建表）、`docs/sql/02_data.sql`（初始化数据）保持同步；
> 表结构明细以 SQL 脚本为准，如两者不一致请以 SQL 脚本为准并回改本文档。

## 1. 基本信息

| 项 | 值 |
| --- | --- |
| 数据库名 | `enterprise_platform` |
| 字符集 | `utf8mb4` |
| 排序规则 | `utf8mb4_unicode_ci` |
| 引擎 | `InnoDB` |
| 表命名 | `snake_case`，前缀 `sys_` |
| 主键 | `BIGINT` 自增 |
| 时间字段 | `DATETIME` |
| 逻辑删除 | `deleted`：0 正常，1 删除 |

脚本位置：

```text
docs/sql/01_schema.sql   # 建库建表
docs/sql/02_data.sql     # 初始化数据
```

## 2. 表清单

| 表名 | 用途 | 逻辑删除 | 备注 |
| --- | --- | --- | --- |
| sys_user | 用户账号与资料 | 是 | 含登录审计字段 |
| sys_role | 角色 | 是 | ADMIN / USER 等 |
| sys_permission | 权限标识（与 `@SaCheckPermission` 一致） | 是 | 类型含菜单/按钮/接口 |
| sys_menu | 前端菜单/路由 | 是 | 可关联 permission_code |
| sys_user_role | 用户-角色关联 | 否（关联表） | 联合唯一 (user_id, role_id) |
| sys_role_permission | 角色-权限关联 | 否（关联表） | 联合唯一 (role_id, permission_id) |
| sys_dict | 字典类型 | 是 | dict_type 唯一 |
| sys_dict_item | 字典项 | 是 | (dict_type, item_value) 唯一 |
| sys_config | 系统参数 | 是 | config_key 唯一 |
| sys_login_log | 登录日志 | 否（按时间归档） | 无审计字段 |
| sys_operation_log | 操作日志 | 否（按时间归档） | 无审计字段 |

## 3. 表结构明细

> 业务表公共字段：`created_at`、`updated_at`、`created_by`、`updated_by`、`deleted`，定义见「9. 与 BaseEntity 字段对齐」。
> 关联表与日志表不继承完整公共字段，按各自定义。

### 3.1 sys_user 系统用户

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| username | VARCHAR(64) | 否 | — | 登录用户名（唯一） |
| password | VARCHAR(128) | 否 | — | BCrypt 密码密文 |
| nickname | VARCHAR(64) | 是 | NULL | 昵称 |
| real_name | VARCHAR(64) | 是 | NULL | 真实姓名 |
| email | VARCHAR(128) | 是 | NULL | 邮箱（可空唯一） |
| phone | VARCHAR(20) | 是 | NULL | 手机号（可空唯一） |
| avatar | VARCHAR(512) | 是 | NULL | 头像 URL |
| gender | TINYINT | 是 | 0 | 性别：0 未知，1 男，2 女 |
| status | TINYINT | 否 | 1 | 状态：0 禁用，1 启用 |
| remark | VARCHAR(512) | 是 | NULL | 备注 |
| last_login_at | DATETIME | 是 | NULL | 最后登录时间 |
| last_login_ip | VARCHAR(64) | 是 | NULL | 最后登录 IP |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| created_by | BIGINT | 是 | NULL | 创建人 |
| updated_by | BIGINT | 是 | NULL | 更新人 |
| deleted | TINYINT | 否 | 0 | 逻辑删除：0 正常，1 删除 |

### 3.2 sys_role 系统角色

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| role_code | VARCHAR(64) | 否 | — | 角色编码，如 ADMIN（唯一） |
| role_name | VARCHAR(64) | 否 | — | 角色名称 |
| sort | INT | 否 | 0 | 排序（越小越靠前） |
| status | TINYINT | 否 | 1 | 状态：0 禁用，1 启用 |
| remark | VARCHAR(512) | 是 | NULL | 备注 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| created_by | BIGINT | 是 | NULL | 创建人 |
| updated_by | BIGINT | 是 | NULL | 更新人 |
| deleted | TINYINT | 否 | 0 | 逻辑删除：0 正常，1 删除 |

### 3.3 sys_permission 系统权限

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| permission_code | VARCHAR(128) | 否 | — | 权限标识，如 `system:user:list`（唯一） |
| permission_name | VARCHAR(64) | 否 | — | 权限名称 |
| permission_type | TINYINT | 否 | 1 | 类型：1 菜单，2 按钮，3 接口 |
| parent_id | BIGINT | 否 | 0 | 父权限 ID，0 为顶级 |
| path | VARCHAR(256) | 是 | NULL | 路由/资源路径 |
| method | VARCHAR(16) | 是 | NULL | HTTP 方法，接口权限用 |
| sort | INT | 否 | 0 | 排序 |
| status | TINYINT | 否 | 1 | 状态：0 禁用，1 启用 |
| remark | VARCHAR(512) | 是 | NULL | 备注 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| created_by | BIGINT | 是 | NULL | 创建人 |
| updated_by | BIGINT | 是 | NULL | 更新人 |
| deleted | TINYINT | 否 | 0 | 逻辑删除：0 正常，1 删除 |

### 3.4 sys_menu 系统菜单

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| parent_id | BIGINT | 否 | 0 | 父菜单 ID，0 为顶级 |
| menu_name | VARCHAR(64) | 否 | — | 菜单名称 |
| menu_type | TINYINT | 否 | 1 | 类型：1 目录，2 菜单，3 按钮 |
| path | VARCHAR(256) | 是 | NULL | 路由 path |
| component | VARCHAR(256) | 是 | NULL | 前端组件路径 |
| permission_code | VARCHAR(128) | 是 | NULL | 关联权限标识 |
| icon | VARCHAR(64) | 是 | NULL | 图标 |
| sort | INT | 否 | 0 | 排序 |
| visible | TINYINT | 否 | 1 | 是否可见：0 隐藏，1 显示 |
| status | TINYINT | 否 | 1 | 状态：0 禁用，1 启用 |
| remark | VARCHAR(512) | 是 | NULL | 备注 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| created_by | BIGINT | 是 | NULL | 创建人 |
| updated_by | BIGINT | 是 | NULL | 更新人 |
| deleted | TINYINT | 否 | 0 | 逻辑删除：0 正常，1 删除 |

### 3.5 sys_user_role 用户-角色关联

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 否 | — | 用户 ID |
| role_id | BIGINT | 否 | — | 角色 ID |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |

### 3.6 sys_role_permission 角色-权限关联

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| role_id | BIGINT | 否 | — | 角色 ID |
| permission_id | BIGINT | 否 | — | 权限 ID |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |

### 3.7 sys_dict 字典类型

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| dict_type | VARCHAR(64) | 否 | — | 字典类型编码（唯一） |
| dict_name | VARCHAR(64) | 否 | — | 字典名称 |
| status | TINYINT | 否 | 1 | 状态：0 禁用，1 启用 |
| remark | VARCHAR(512) | 是 | NULL | 备注 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| created_by | BIGINT | 是 | NULL | 创建人 |
| updated_by | BIGINT | 是 | NULL | 更新人 |
| deleted | TINYINT | 否 | 0 | 逻辑删除：0 正常，1 删除 |

### 3.8 sys_dict_item 字典项

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| dict_type | VARCHAR(64) | 否 | — | 字典类型编码 |
| item_label | VARCHAR(64) | 否 | — | 显示标签 |
| item_value | VARCHAR(64) | 否 | — | 存储值 |
| sort | INT | 否 | 0 | 排序 |
| status | TINYINT | 否 | 1 | 状态：0 禁用，1 启用 |
| remark | VARCHAR(512) | 是 | NULL | 备注 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| created_by | BIGINT | 是 | NULL | 创建人 |
| updated_by | BIGINT | 是 | NULL | 更新人 |
| deleted | TINYINT | 否 | 0 | 逻辑删除：0 正常，1 删除 |

### 3.9 sys_config 系统参数

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| config_key | VARCHAR(128) | 否 | — | 参数键（唯一） |
| config_value | VARCHAR(1024) | 否 | — | 参数值 |
| config_name | VARCHAR(64) | 否 | — | 参数名称 |
| config_type | TINYINT | 否 | 0 | 类型：0 系统内置，1 业务自定义 |
| remark | VARCHAR(512) | 是 | NULL | 备注 |
| created_at | DATETIME | 否 | CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | 否 | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP | 更新时间 |
| created_by | BIGINT | 是 | NULL | 创建人 |
| updated_by | BIGINT | 是 | NULL | 更新人 |
| deleted | TINYINT | 否 | 0 | 逻辑删除：0 正常，1 删除 |

### 3.10 sys_login_log 登录日志

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 是 | NULL | 用户 ID |
| username | VARCHAR(64) | 是 | NULL | 登录用户名 |
| login_type | TINYINT | 否 | 1 | 登录类型：1 账号密码，2 其他 |
| status | TINYINT | 否 | — | 结果：0 失败，1 成功 |
| ip | VARCHAR(64) | 是 | NULL | IP |
| user_agent | VARCHAR(512) | 是 | NULL | User-Agent |
| message | VARCHAR(256) | 是 | NULL | 消息/失败原因 |
| login_at | DATETIME | 否 | CURRENT_TIMESTAMP | 登录时间 |

### 3.11 sys_operation_log 操作日志

| 字段 | 类型 | 可空 | 默认 | 说明 |
| --- | --- | --- | --- | --- |
| id | BIGINT | 否 | AUTO_INCREMENT | 主键 |
| user_id | BIGINT | 是 | NULL | 操作人 ID |
| username | VARCHAR(64) | 是 | NULL | 操作人用户名 |
| module | VARCHAR(64) | 是 | NULL | 模块 |
| operation | VARCHAR(64) | 是 | NULL | 操作类型 |
| method | VARCHAR(16) | 是 | NULL | HTTP 方法 |
| request_uri | VARCHAR(512) | 是 | NULL | 请求 URI |
| request_id | VARCHAR(64) | 是 | NULL | 请求 ID |
| request_params | TEXT | 是 | NULL | 请求参数（脱敏后） |
| ip | VARCHAR(64) | 是 | NULL | IP |
| status | TINYINT | 否 | 1 | 结果：0 失败，1 成功 |
| error_msg | VARCHAR(1024) | 是 | NULL | 错误信息 |
| duration_ms | INT | 是 | NULL | 耗时毫秒 |
| operated_at | DATETIME | 否 | CURRENT_TIMESTAMP | 操作时间 |

## 4. 核心关系

```text
User ──< UserRole >── Role ──< RolePermission >── Permission
  │
  └── LoginLog

Menu（可关联 permission_code）
Dict ── DictItem
Config
OperationLog
```

## 5. 关键索引与唯一约束

> 索引名与 `01_schema.sql` 中的定义一致。

| 表 | 唯一键 | 索引键 |
| --- | --- | --- |
| sys_user | uk_username(username)、uk_phone(phone)、uk_email(email) | idx_status(status)、idx_created_at(created_at) |
| sys_role | uk_role_code(role_code) | idx_status(status) |
| sys_permission | uk_permission_code(permission_code) | idx_parent_id(parent_id)、idx_status(status) |
| sys_menu | — | idx_parent_id(parent_id)、idx_permission_code(permission_code)、idx_status(status) |
| sys_user_role | uk_user_role(user_id, role_id) | idx_role_id(role_id) |
| sys_role_permission | uk_role_permission(role_id, permission_id) | idx_permission_id(permission_id) |
| sys_dict | uk_dict_type(dict_type) | — |
| sys_dict_item | uk_dict_type_value(dict_type, item_value) | idx_dict_type(dict_type) |
| sys_config | uk_config_key(config_key) | — |
| sys_login_log | — | idx_user_id(user_id)、idx_username(username)、idx_login_at(login_at)、idx_status(status) |
| sys_operation_log | — | idx_user_id(user_id)、idx_operated_at(operated_at)、idx_module(module)、idx_request_id(request_id) |

## 6. 权限标识约定

与代码注解保持一致，例如：

```text
system:user:list
system:user:add
system:user:update
system:user:delete
system:user:export
system:user:status
system:user:password
```

```java
// @SaCheckPermission("system:user:list")
```

## 7. 初始化账号

| 用户名 | 密码（明文） | 角色 | 说明 |
| --- | --- | --- | --- |
| admin | 123456 | ADMIN | 全部权限 |
| user | 123456 | USER | 仅部分 list 权限 |

密码在库中为 **BCrypt** 密文（`$2b$10$...`），禁止存明文 / MD5 / SHA1。

Spring 校验示例：

```java
// new BCryptPasswordEncoder().matches("123456", passwordFromDb);
```

## 8. 执行方式

### Linux / macOS

```bash
mysql -uroot -p < docs/sql/01_schema.sql
mysql -uroot -p < docs/sql/02_data.sql
```

### Windows

```cmd
mysql -uroot -p < docs\sql\01_schema.sql
mysql -uroot -p < docs\sql\02_data.sql
```

或在客户端中依次执行两个脚本。

### 验证

```sql
USE enterprise_platform;
SHOW TABLES;
SELECT id, username, status FROM sys_user WHERE deleted = 0;
SELECT role_code FROM sys_role;
SELECT permission_code FROM sys_permission LIMIT 10;
SELECT u.username, r.role_code
FROM sys_user u
JOIN sys_user_role ur ON u.id = ur.user_id
JOIN sys_role r ON ur.role_id = r.id;
```

## 9. 与 BaseEntity 字段对齐

业务表公共字段与 `com.example.enterprise.common.core.entity.BaseEntity` 一致：

| 列 | Java 字段 | 说明 |
| --- | --- | --- |
| created_at | createdAt | 插入自动填充 |
| updated_at | updatedAt | 插入/更新自动填充 |
| created_by | createdBy | 当前用户 |
| updated_by | updatedBy | 当前用户 |
| deleted | deleted | @TableLogic，0/1 |

日志表不继承业务软删除策略，按时间查询与归档。

## 10. 设计原则摘要

1. 简单业务表统一软删除；日志表物理保留、按时间清理。
2. 权限字符串是「约定 + 数据」，不是框架自动生成。
3. 关联表使用联合唯一索引，避免重复授权。
4. 手机号、邮箱允许 NULL，但仍用唯一索引（MySQL 中多个 NULL 不冲突）。
5. 敏感配置（数据库密码等）不入库明文业务配置表的说明字段；真正密钥走环境变量 / Nacos。
