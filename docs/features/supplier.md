# 供应商模块（Supplier Module）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

供应商是采购主数据（原料树脂、包装材料、添加剂等供应方）。标准 CRUD + 启停状态，归类于「基础数据」。

## 2. 数据模型（PostgreSQL）

`supplier` 表（迁移见 [`V4__add_master_data.sql`](../../api/src/main/resources/db/migration/V4__add_master_data.sql)）：

| 字段 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK | 主键 |
| code | VARCHAR(32) | 必填,唯一(uk_supplier_code_deleted),创建后不可改 | 供应商编码 |
| name | VARCHAR(128) | 必填,唯一(uk_supplier_name_deleted) | 供应商名称 |
| address | VARCHAR(255) | 必填 | 地址 |
| contact / phone / email / remark | VARCHAR | 可空 | 联系人 / 电话 / 邮箱 / 备注 |
| status | SMALLINT | 默认 1 | 0=禁用,1=启用 |
| created_at / updated_at / created_by / updated_by | —— | 审计 | |
| deleted | SMALLINT | `@TableLogic` | 逻辑删除 |

## 3. 接口契约（6 个端点）

契约源:[`api-contract/openapi.yaml`](../../api-contract/openapi.yaml)。

| 方法 | 路径 | operationId | 成功码 |
| --- | --- | --- | --- |
| POST | /api/suppliers | createSupplier | 201 |
| GET | /api/suppliers | listSuppliers | 200 |
| GET | /api/suppliers/{id} | getSupplierById | 200 |
| PUT | /api/suppliers/{id} | updateSupplier | 200 |
| DELETE | /api/suppliers/{id} | deleteSupplier | 204 |
| PATCH | /api/suppliers/{id}/status | updateSupplierStatus | 200 |

列表支持 `keyword`（名称/编码/联系人模糊）、`status` 过滤。错误码:`DUPLICATE_SUPPLIER_NAME` / `DUPLICATE_SUPPLIER_CODE`(409)、`SUPPLIER_NOT_FOUND`(404)、`VALIDATION_ERROR`(400)。

## 4. 关键设计

- `code`、`name` 全局唯一;`code` 创建后不可改;更新时校验改名不与他人重名。
- 状态变更走独立 `PATCH .../status`，共用契约 `UpdateStatusRequest`。
- 删除为逻辑删除;唯一索引带 `deleted`，删除后业务键可复用。

## 5. 实现

- **Contract → Backend → Frontend** 三段式;后端 13 文件(entity/dto×3/vo×3/mapper+xml/converter/service+impl/controller),前端 types/api/列表页/表单弹窗/搜索表单。
- 前端经真实后端 + 种子数据浏览器验证：列表渲染、查询、启停、新建/编辑弹窗均正常。

## 6. 测试数据与测试

- 种子（[`scripts/seed-master-data.mjs`](../../scripts/seed-master-data.mjs)）:`SUP-001` 中石化华东树脂、`SUP-002` 江苏华美包装、`SUP-003` 宁波塑源添加剂。
- 后端 `SupplierControllerTest`(MockMvc + 真实 PostgreSQL)8 用例:创建默认值、重名/重码 409、必填 400、列表、状态变更、404、删除后 404。
- 前端 `api/supplier.test.ts` 断言 6 端点;契约 `validate.mjs` 断言端点齐全。
