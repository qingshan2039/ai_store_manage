# 库区模块（Zone Module）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

库区是仓库内的存储分区（如收货暂存区、存储区、拣货区、发货月台）。**库区隶属仓库（多对一）**，归类于「基础数据」。

## 2. 数据模型（PostgreSQL）

`zone` 表（迁移见 [`V4__add_master_data.sql`](../../api/src/main/resources/db/migration/V4__add_master_data.sql)）：

| 字段 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK | 主键 |
| warehouse_id | BIGINT | 必填,索引 | 所属仓库 |
| code | VARCHAR(32) | 必填,**仓库内唯一**(uk_zone_wh_code_deleted: warehouse_id+code+deleted),创建后不可改 | 库区编码 |
| name | VARCHAR(64) | 必填 | 库区名称 |
| type | VARCHAR(32) | 可空 | 类型（存储/拣货/暂存…） |
| remark | VARCHAR(500) | 可空 | 备注 |
| status | SMALLINT | 默认 1 | 0=禁用,1=启用 |
| created_at / updated_at / created_by / updated_by | —— | 审计 | |
| deleted | SMALLINT | `@TableLogic` | 逻辑删除 |

## 3. 接口契约（6 个端点）

| 方法 | 路径 | operationId | 成功码 |
| --- | --- | --- | --- |
| POST | /api/zones | createZone | 201 |
| GET | /api/zones | listZones | 200 |
| GET | /api/zones/{id} | getZoneById | 200 |
| PUT | /api/zones/{id} | updateZone | 200 |
| DELETE | /api/zones/{id} | deleteZone | 204 |
| PATCH | /api/zones/{id}/status | updateZoneStatus | 200 |

响应内嵌 `warehouseName`（关联仓库名）。列表支持 `keyword`（名称/编码）、`warehouseId`、`status` 过滤。错误码:`DUPLICATE_ZONE_CODE`(409，仓库内编码冲突)、`ZONE_NOT_FOUND`(404)、`VALIDATION_ERROR`(400)。

## 4. 关键设计

- **编码在仓库内唯一**:唯一约束为 `(warehouse_id, code, deleted)`，因此**不同仓库可用相同库区编码**（如各仓都有 `Z-A`）。
- **关联仓库名**:Service 注入 `WarehouseMapper`，单条查询用 `selectById`、列表用 `selectBatchIds` 批量加载本页库区的仓库名（避免 N+1），由 `ZoneConverter` 注入 VO 的 `warehouseName`。
- `warehouseId` 与 `code` 创建后不可改（`UpdateZoneRequest` 仅含 name/type/remark）。
- 前端表单的「所属仓库」下拉与搜索过滤项均由 `warehouseApi.list` 实时加载选项。

## 5. 实现

- 后端 13 文件;`ZoneServiceImpl` 处理仓库内唯一校验与仓库名关联。前端列表页加载仓库选项并下传给搜索表单与表单弹窗。
- 浏览器验证:5 条库区列表的「所属仓库」列正确显示（原料仓库/半成品仓库/成品仓库），新建弹窗仓库下拉可选。

## 6. 测试数据与测试

- 种子:WH-RAW(Z-A 收货暂存区, Z-B 树脂存储区)、WH-SEMI(Z-A 母卷存储区)、WH-FIN(Z-A 成品拣货区, Z-B 发货月台区)。
- 后端 `ZoneControllerTest` 7 用例:创建含 warehouseName、**同仓重码 409**、**异仓同码 201**、缺 warehouseId 400、按仓库过滤、404、删除后 404。
- 前端 `api/zone.test.ts` 断言 6 端点（含隶属仓库 payload）。
