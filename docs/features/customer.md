# 顾客模块（Customer Module）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

顾客（客户公司）是业务主数据，记录客户公司的基础信息与**收/发货地址（ship-to）**。归类于「基础数据」。

## 2. 数据模型 `customer`（PostgreSQL）

| 字段 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK,自增 | 主键 |
| code | VARCHAR(32) | 必填,唯一(uk_customer_code_deleted),创建后不可改 | 客户编码 |
| name | VARCHAR(128) | 必填,唯一(uk_customer_name_deleted) | 客户公司名称 |
| address | VARCHAR(255) | 必填 | 客户公司地址（注册/账单地址） |
| ship_address | VARCHAR(255) | 必填 | **收/发货地址（ship-to）** |
| contact | VARCHAR(64) | 可空 | 联系人 |
| phone | VARCHAR(32) | 可空 | 联系电话 |
| email | VARCHAR(128) | 可空 | 邮箱 |
| remark | VARCHAR(500) | 可空 | 备注 |
| status | SMALLINT | 默认 1 | 0=禁用,1=启用 |
| created_at / updated_at | TIMESTAMP | 自动填充 | 审计 |
| created_by / updated_by | BIGINT | 可空 | 审计 |
| deleted | SMALLINT | 默认 0,`@TableLogic` | 逻辑删除 |

唯一索引含 `deleted`,逻辑删除后可复用 code / name。建表见 Flyway 迁移 [`V2__add_customer.sql`](../../api/src/main/resources/db/migration/V2__add_customer.sql)。

## 3. 接口契约（6 个端点）

契约源:[`api-contract/openapi.yaml`](../../api-contract/openapi.yaml)。

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

- `code`、`name` 均全局唯一;`code` 创建后不可修改(对齐用户/部门的不可变业务键)。
- 必填:`code`、`name`、`address`、`shipAddress`;其余(联系人/电话/邮箱/备注)选填。
- `shipAddress`(收/发货地址)与 `address`(公司地址)分开,对应账单地址 vs 收货地址。
- 状态变更走独立 `PATCH .../status` 接口;删除为逻辑删除(暂无引用方,故无占用校验)。
- 列表 `keyword` 跨 `name`/`code`/`contact` 模糊匹配。

## 5. 实现过程（Contract → Backend → Frontend）

- **Contract**:`openapi.yaml` 增加顾客 tag、6 path、6 schema,复用统一 `ErrorResponse`;`api-contract` 校验通过(10 path/22 schema/14 端点)。
- **Backend**:`module/customer/`(entity/dto/vo/mapper+xml/converter/service/controller)+ 异常工厂 + **Flyway V2** 建表。验证:`mvn compile`、真实 PostgreSQL 端到端 curl 全过。
- **Frontend**:`types/customer.ts`、`api/customer.ts`、`pages/customer/`(列表 + 新增/编辑弹窗 + 搜索),接入「基础数据 > 顾客管理」菜单/路由/面包屑/enums。验证:`tsc -b && vite build` 通过。

## 6. 数据库（Flyway V2）

V1 为基线不可改,顾客建表作为递增迁移 `V2__add_customer.sql`。应用启动时 Flyway 自动从 V1 迁移到 V2;详见总体设计「[数据库初始化与迁移](../design/overview.md)」。

## 7. 测试

- **后端**:`module/customer/CustomerControllerTest`(MockMvc + 真实 PostgreSQL),覆盖创建/详情/更新/状态/列表/删除与 400/404/409,共 7 个用例。
- **契约**:`validate.mjs` 断言顾客 6 端点齐全。
- **前端**:`api/customer.test.ts` 断言 customerApi 的方法与 URL。
- 运行见 [测试指南](../design/testing.md)。全绿:后端 22、契约 14 端点、前端 22。
