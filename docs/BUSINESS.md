# 业务服务说明（Phase 14）

## 定位

`enterprise-business` 承载**领域业务**示例。本阶段以「工单」演示标准分层与状态流转，真实项目可替换为订单、审批等域模型。

## 端口 / 服务名

- 端口：**8085**
- 服务名：`enterprise-business`
- 网关前缀：`/api/biz/**`

## 接口

| 方法   | 路径                               | 说明     |
|--------|------------------------------------|----------|
| GET    | `/api/biz/work-orders`             | 分页     |
| GET    | `/api/biz/work-orders/{id}`        | 详情     |
| POST   | `/api/biz/work-orders`             | 创建     |
| PUT    | `/api/biz/work-orders/{id}`        | 更新     |
| PUT    | `/api/biz/work-orders/{id}/status` | 状态变更 |
| DELETE | `/api/biz/work-orders/{id}`        | 删除     |

需登录。

## 状态

| 值 | 含义   |
|----|--------|
| 0  | 待处理 |
| 1  | 处理中 |
| 2  | 已完成 |
| 3  | 已关闭 |

## SQL

```bash
mysql -uroot -p enterprise_platform < docs/sql/04_business.sql
```
