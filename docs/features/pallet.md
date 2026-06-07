# 托盘类型模块（Pallet Type Module）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

托盘类型是仓储承载主数据，按 **ISO 6780 国际标准**预置大/中/小三种规格。归类于「基础数据」。

| 规格 | 长×宽(mm) | 标准 |
| --- | --- | --- |
| 大托盘 | 1200 × 1000 | ISO6780（北美/通用） |
| 中/欧标托盘 | 1200 × 800 | ISO6780 / 欧标 EUR1（EPAL） |
| 小托盘 | 800 × 600 | ISO6780 半欧托（EUR6） |

## 2. 数据模型（PostgreSQL）

`pallet_type` 表（迁移见 [`V4__add_master_data.sql`](../../api/src/main/resources/db/migration/V4__add_master_data.sql)）：

| 字段 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK | 主键 |
| code | VARCHAR(32) | 必填,唯一(uk_pallet_type_code_deleted),创建后不可改 | 托盘编码 |
| name | VARCHAR(64) | 必填,唯一(uk_pallet_type_name_deleted) | 托盘名称 |
| length / width | NUMERIC(10,2) | 必填 | 长 / 宽(mm) |
| tare_weight | NUMERIC(10,2) | 可空 | 皮重(kg) |
| max_load | NUMERIC(10,2) | 可空 | 最大载重(kg) |
| max_stack | INTEGER | 可空 | 最大堆叠层 |
| remark | VARCHAR(500) | 可空 | 备注 |
| status | SMALLINT | 默认 1 | 0=禁用,1=启用 |
| created_at / updated_at / created_by / updated_by | —— | 审计 | |
| deleted | SMALLINT | `@TableLogic` | 逻辑删除 |

数值字段实体侧用 `BigDecimal`（length/width/tareWeight/maxLoad）与 `Integer`（maxStack）。

## 3. 接口契约（6 个端点）

| 方法 | 路径 | operationId | 成功码 |
| --- | --- | --- | --- |
| POST | /api/pallet-types | createPalletType | 201 |
| GET | /api/pallet-types | listPalletTypes | 200 |
| GET | /api/pallet-types/{id} | getPalletTypeById | 200 |
| PUT | /api/pallet-types/{id} | updatePalletType | 200 |
| DELETE | /api/pallet-types/{id} | deletePalletType | 204 |
| PATCH | /api/pallet-types/{id}/status | updatePalletTypeStatus | 200 |

`length`、`width` 创建必填且须为正。列表支持 `keyword`（名称/编码）、`status` 过滤，按长度降序（大→小）。错误码:`DUPLICATE_PALLET_TYPE_NAME` / `DUPLICATE_PALLET_TYPE_CODE`(409)、`PALLET_TYPE_NOT_FOUND`(404)、`VALIDATION_ERROR`(400)。

## 4. 关键设计

- 三种规格按 **ISO 6780** 预置，通过种子脚本灌入（非迁移内置，保持模块测试空表假设）。
- 尺寸/载重用 `NUMERIC(10,2)` / `BigDecimal` 保证精度;`max_stack` 为整数。
- `code`、`name` 全局唯一;`code` 创建后不可改。前端表单用 antd `InputNumber`（堆叠层 `precision=0`）。

## 5. 实现

- 后端 13 文件;前端 types/api/列表页(规格列 `长×宽`)/表单(数值输入)/搜索。
- 浏览器验证:列表展示 3 种 ISO 托盘，启停、新建/编辑弹窗正常。

## 6. 测试数据与测试

- 种子:`PLT-L` 大托盘 1200×1000、`PLT-M` 欧标托盘 1200×800、`PLT-S` 小托盘 800×600（含皮重/载重/堆叠层）。
- 后端 `PalletTypeControllerTest` 7 用例:含尺寸创建、重名/重码 409、缺 length 400、列表、404、删除后 404。
- 前端 `api/pallet.test.ts` 断言 6 端点（含 ISO 规格 payload）。
