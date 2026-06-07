# 顾客模块（Customer Module）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

顾客（客户公司）是业务主数据。**同一连锁客户可有多个收/发货地址（ship-to）**，每个地址带 `remark`（如客户报错地址后填写的修正说明）。归类于「基础数据」。

## 2. 数据模型（PostgreSQL）

### 2.1 `customer`（客户主表）

| 字段 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK | 主键 |
| code | VARCHAR(32) | 必填,唯一(uk_customer_code_deleted),创建后不可改 | 客户编码 |
| name | VARCHAR(128) | 必填,唯一(uk_customer_name_deleted) | 客户公司名称 |
| address | VARCHAR(255) | 必填 | 客户公司地址（注册/账单地址） |
| contact / phone / email / remark | VARCHAR | 可空 | 联系人 / 电话 / 邮箱 / 备注 |
| status | SMALLINT | 默认 1 | 0=禁用,1=启用 |
| created_at / updated_at / created_by / updated_by | —— | 审计 | |
| deleted | SMALLINT | `@TableLogic` | 逻辑删除 |

> 单值 `ship_address` 已移除，改为下方子表（V3 迁移）。

### 2.2 `customer_ship_address`（送货地址子表，一对多）

| 字段 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK | 主键 |
| customer_id | BIGINT | 必填,索引 | 所属客户 |
| address | VARCHAR(255) | 必填 | 收/发货地址 |
| remark | VARCHAR(255) | 可空 | **送货地址备注（客户报错地址后的修正说明）** |
| created_at / updated_at | TIMESTAMP | 自动填充 | 审计 |

迁移见 [`V2__add_customer.sql`](../../api/src/main/resources/db/migration/V2__add_customer.sql)（建主表）与 [`V3__customer_ship_address.sql`](../../api/src/main/resources/db/migration/V3__customer_ship_address.sql)（建子表 + 迁移旧单值 + 删旧列）。

## 3. 接口契约（6 个端点）

契约源:[`api-contract/openapi.yaml`](../../api-contract/openapi.yaml)。端点不变;请求/响应中 `shipAddress`（单值）已改为 **`shipAddresses` 数组**：

- 响应元素 `ShipAddress { id, address, remark }`；
- 请求元素 `ShipAddressInput { address, remark }`，创建必填且至少 1 个，更新提供则整列表替换。

| 方法 | 路径 | operationId | 成功码 |
| --- | --- | --- | --- |
| POST | /api/customers | createCustomer | 201 |
| GET | /api/customers | listCustomers | 200 |
| GET | /api/customers/{id} | getCustomerById | 200 |
| PUT | /api/customers/{id} | updateCustomer | 200 |
| DELETE | /api/customers/{id} | deleteCustomer | 204 |
| PATCH | /api/customers/{id}/status | updateCustomerStatus | 200 |

错误码:`DUPLICATE_CUSTOMER_NAME` / `DUPLICATE_CUSTOMER_CODE`(409)、`CUSTOMER_NOT_FOUND`(404)、`VALIDATION_ERROR`(400)。

## 4. 关键设计决策

- **一对多送货地址**:解决连锁客户多门店地址不一致 —— 地址存于 `customer_ship_address` 子表，API 内嵌 `shipAddresses` 数组。
- **整列表替换**:更新时若提供 `shipAddresses`，删除旧子表记录后整体重插（语义简单）；不提供则不动地址。
- **每条地址带 `remark`**:用于客户报错地址后记录修正说明。
- `code`、`name` 全局唯一;`code` 创建后不可改。删除为主表逻辑删除 + 子表物理删除。
- 列表查询批量加载本页顾客的送货地址（一次 `IN` 查询，避免 N+1）。

## 5. 实现过程

- **Contract**:`shipAddress`→`shipAddresses` 数组 + 新增 `ShipAddress`/`ShipAddressInput` schema;`api-contract` 校验通过(24 schema)。
- **Backend**:新增 `CustomerShipAddress` 实体/Mapper;Service 处理增删查与替换;**Flyway V3** 改造表结构。真实 PostgreSQL 端到端 curl 全过（含 3 地址连锁客户、整列表替换、必填校验）。
- **Frontend**:表单用 antd `Form.List` 增减多条「地址 + 备注」;列表逐行展示多地址。`tsc -b && vite build` 通过。

## 6. 测试

- **后端**:`CustomerControllerTest`(MockMvc + 真实 PostgreSQL),覆盖单/多地址创建、整列表替换、`shipAddresses` 必填、409/404 等,共 9 个用例。
- **契约**:`validate.mjs` 断言顾客 6 端点齐全。
- **前端**:`api/customer.test.ts` 断言 customerApi（含多地址 payload）。
- 全绿:后端 24、契约 14 端点、前端 22。运行见 [测试指南](../design/testing.md)。

## 7. 演进记录

- v1:单值 `ship_address`（V2）。
- v2:改为一对多 `customer_ship_address` 子表 + 每条 `remark`（V3），支持连锁客户多送货地址。
