# 文件服务说明（Phase 13）

## 职责

- 上传 / 下载 / 删除 / 元数据查询
- 本地磁盘存储（抽象可扩展 MinIO/OSS）
- 元数据表 `sys_file`

## 端口

**8084**，服务名 `enterprise-file`

## 接口

| 方法   | 路径                       | 说明                                  |
|--------|----------------------------|---------------------------------------|
| POST   | `/api/files/upload`        | multipart 字段 `file`，可选 `bizType` |
| GET    | `/api/files/{id}`          | 元数据                                |
| GET    | `/api/files/{id}/download` | 下载                                  |
| DELETE | `/api/files/{id}`          | 删除（逻辑删 + 删物理文件）           |

均需登录（`Authorization: Bearer <token>`）。

## 配置

| 配置项                           | 默认           | 说明       |
|----------------------------------|----------------|------------|
| `file.storage.local-base-dir`    | `./data/files` | 本地根目录 |
| `file.upload.max-size`           | 10485760       | 10MB       |
| `file.upload.allowed-extensions` | jpg,png,pdf... | 后缀白名单 |

## 初始化 SQL

```bash
mysql -uroot -p enterprise_platform < docs/sql/03_file.sql
```
