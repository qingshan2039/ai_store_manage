# 总体设计（System Design）

> AI 门店管理系统(WMS)——系统级总体设计。模块级的功能设计见 [`docs/features/`](../features/)。

## 1. 系统简介

面向仓储/门店运营的后台管理系统(WMS),提供用户、部门等基础管理能力,并为商品、仓库、供应商、库存(出入库/调拨/盘点)等业务模块预留扩展。

## 2. 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 21、Spring Boot 3.4.5、MyBatis-Plus 3.5.7、PostgreSQL 16、Flyway(数据库迁移)、Spring Security(仅 BCrypt)、Validation、Lombok、Actuator |
| 前端 | React 19、TypeScript、Vite、Ant Design 6、Zustand、React Router 7、axios、dayjs |
| 契约 | OpenAPI 3.0.3 |
| 测试 | JUnit 5 + Spring MockMvc(后端,真实 PostgreSQL)、swagger-parser(契约)、Vitest + Testing Library(前端) |

## 3. 总体架构

单仓库(monorepo)、契约优先(Contract First):

```
ai_store_manage/
├── api-contract/   # OpenAPI 契约(唯一事实来源)
├── api/            # Spring Boot 后端
├── web/            # React 前端
└── docs/           # 文档(本目录)
```

数据流:前端(axios)→ `/api/*`(开发期 vite 代理到 :8080)→ Spring Boot Controller → Service → MyBatis-Plus Mapper → PostgreSQL(本地开发用 Docker 容器,端口 5433)。

## 4. 后端分层与包结构

`com.aistore` 下按模块划分:

- `config/` 全局配置(MyBatis-Plus 分页、字段自动填充、Jackson、Security)
- `common/` 公共组件(统一异常、统一错误响应 `ErrorResponse` / `FieldError`)
- `module/<模块>/` 业务模块(controller / service / mapper / entity / dto / vo / converter [/ enums])

前端按 `api/`、`types/`、`pages/<模块>/`、`components/`、`hooks/`、`stores/`、`router/`、`constants/` 组织。

## 5. 模块清单

| 模块 | 状态 | 功能设计 |
| --- | --- | --- |
| 用户管理 | ✅ 已实现 | [features/user.md](../features/user.md) |
| 部门管理 | ✅ 已实现 | [features/department.md](../features/department.md) |
| 商品 / 仓库 / 供应商 | 🚧 预留 | — |
| 库存(查询/出入库/调拨/盘点) | 🚧 预留 | — |

## 6. 统一约定

- **RESTful**:资源路径 `/api/<resource>`;创建 201、查询 200、删除 204、状态变更用 PATCH。
- **分页**:`page`(从 1 开始,默认 1)、`pageSize`(默认 20,上限 100);列表响应统一 `{ items, total, page, pageSize, totalPages }`。
- **错误响应**:统一 `ErrorResponse { code, message, details? }`,`code` 为大写下划线错误码;字段校验错误放 `details: FieldError[]`。
- **逻辑删除**:实体含 `deleted` 字段(`@TableLogic`),删除即置 1;唯一索引带 `deleted`,删除后可复用业务键。
- **审计字段**:`createdAt` / `updatedAt`(自动填充)、`createdBy` / `updatedBy`。
- **状态枚举**:`status` 0=禁用、1=启用。
- **敏感字段**:`password`、`deleted` 永不出现在响应中。

## 7. 数据库初始化与迁移（Flyway）

- 版本化迁移脚本:`api/src/main/resources/db/migration/V<n>__<desc>.sql`(Flyway 默认位置)。当前基线 [`V1__init_schema.sql`](../../api/src/main/resources/db/migration/V1__init_schema.sql) 含 `sys_user`、`sys_department` 两表与部门种子。
- 启动自动迁移:应用启动时 Flyway 自动执行待应用的迁移,以 `flyway_schema_history` 表跟踪。
- 兼容已有库:`baseline-on-migrate: true`——对"已有表但无 Flyway 历史"的库先建立基线(baseline)再迁移,避免首次报错;全新空库则直接执行 V1 建表灌种子。
- 演进规则:**已发布的迁移文件不可修改**;新的 schema 变更一律新增递增版本 `V2__xxx.sql`、`V3__xxx.sql`……
- 测试:后端集成测试连真实 PostgreSQL(dev 容器内独立库 `ai_store_test`),Flyway 在测试库上同样自动迁移;详见 [testing.md](testing.md)。
- 注意:Flyway 负责"表",不负责"库";`ai_store_manage` 数据库本身需先存在。
- 历史演进:早期用 `spring.sql.init` 跑幂等 `schema.sql`,现已改为 Flyway 版本化管理;背景与根因见 [features/department.md](../features/department.md) 的「问题分析」一节。

## 8. 开发流程规范

详见项目根 [`CLAUDE.md`](../../CLAUDE.md)。核心八条:

1. 先 Contract,后 Backend,再 Frontend;
2. 未验证不进入下一阶段;
3. 未在契约定义不允许实现;
4. 每阶段只改对应目录;
5. 输出完整、可运行、可验证;
6. 中文交流;
7. 完成功能后输出 / 更新文档(本目录);
8. 完成功能后补测试用例并跑通(见 [testing.md](testing.md))。

## 9. 文档组织

- `docs/design/` —— **总体设计**(本目录):系统级架构、技术栈、约定、流程,含 [测试指南 testing.md](testing.md)。
- `docs/features/` —— **功能设计**:按模块拆分的数据模型、接口、设计决策与实现过程。
