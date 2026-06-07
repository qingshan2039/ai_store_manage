# 仓库模块（Warehouse Module）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

仓库是仓储主数据，按用途分**原料仓 / 半成品仓 / 成品仓**三类（对应塑料缠绕膜厂的树脂原料、母卷半成品、成品膜）。仓库下可再分库区（见 [zone.md](zone.md)）。

## 2. 数据模型（PostgreSQL）

`warehouse` 表（迁移见 [`V4__add_master_data.sql`](../../api/src/main/resources/db/migration/V4__add_master_data.sql)）：

| 字段 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK | 主键 |
| code | VARCHAR(32) | 必填,唯一(uk_warehouse_code_deleted),创建后不可改 | 仓库编码 |
| name | VARCHAR(128) | 必填,唯一(uk_warehouse_name_deleted) | 仓库名称 |
| type | VARCHAR(16) | 必填 | 仓库类型 RAW/SEMI/FINISHED |
| remark | VARCHAR(500) | 可空 | 备注 |
| status | SMALLINT | 默认 1 | 0=禁用,1=启用 |
| created_at / updated_at / created_by / updated_by | —— | 审计 | |
| deleted | SMALLINT | `@TableLogic` | 逻辑删除 |

类型枚举 [`WarehouseType`](../../api/src/main/java/com/aistore/module/warehouse/enums/WarehouseType.java) `{ RAW=原料仓, SEMI=半成品仓, FINISHED=成品仓 }`，实体以 String 存储、契约/前端以枚举呈现（前端 `WAREHOUSE_TYPE_MAP` 中文标签 + 彩色 Tag）。

## 3. 接口契约（6 个端点）

| 方法 | 路径 | operationId | 成功码 |
| --- | --- | --- | --- |
| POST | /api/warehouses | createWarehouse | 201 |
| GET | /api/warehouses | listWarehouses | 200 |
| GET | /api/warehouses/{id} | getWarehouseById | 200 |
| PUT | /api/warehouses/{id} | updateWarehouse | 200 |
| DELETE | /api/warehouses/{id} | deleteWarehouse | 204 |
| PATCH | /api/warehouses/{id}/status | updateWarehouseStatus | 200 |

列表支持 `keyword`（名称/编码）、`type`、`status` 过滤。错误码:`DUPLICATE_WAREHOUSE_NAME` / `DUPLICATE_WAREHOUSE_CODE`(409)、`WAREHOUSE_NOT_FOUND`(404)、`VALIDATION_ERROR`(400)。

## 4. 关键设计

- `type` 为必填枚举;缺失或非法触发 400 校验。
- `code`、`name` 全局唯一;`code` 创建后不可改。
- 删除为逻辑删除（注:库区对仓库的引用完整性由业务约定，未加级联）。

## 5. 实现

- 后端 13 文件;Converter 负责枚举 `WarehouseType ↔ String` 转换。前端 types/api/列表页(类型彩色 Tag)/表单(类型下拉)/搜索(类型过滤)。
- 浏览器验证:列表展示 3 仓库且类型 Tag 正确（原料仓/半成品仓/成品仓）。

## 6. 测试数据与测试

- 种子:`WH-RAW` 原料仓库(RAW)、`WH-SEMI` 半成品仓库(SEMI)、`WH-FIN` 成品仓库(FINISHED)。
- 后端 `WarehouseControllerTest` 7 用例:含类型创建、重名/重码 409、缺类型 400、按类型过滤列表、404、删除后 404。
- 前端 `api/warehouse.test.ts` 断言 6 端点（含类型枚举 payload）。
