# 总体设计（System Design）

> AI 门店管理系统(WMS)——系统级总体设计。模块级的功能设计见 [`docs/features/`](../features/)。

## 1. 系统简介

面向仓储/门店运营的后台管理系统(WMS),提供用户、部门等基础管理能力,并为商品、仓库、供应商、库存(出入库/调拨/盘点)等业务模块预留扩展。

## 2. 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 21、Spring Boot 3.4.5、MyBatis-Plus 3.5.7、MySQL 8、Spring Security(仅 BCrypt)、Validation、Lombok、Actuator |
| 前端 | React 19、TypeScript、Vite、Ant Design 6、Zustand、React Router 7、axios、dayjs |
| 契约 | OpenAPI 3.0.3 |

## 3. 总体架构

单仓库(monorepo)、契约优先(Contract First):

```
ai_store_manage/
├── api-contract/   # OpenAPI 契约(唯一事实来源)
├── api/            # Spring Boot 后端
├── web/            # React 前端
└── docs/           # 文档(本目录)
```

数据流:前端(axios)→ `/api/*`(开发期 vite 代理到 :8080)→ Spring Boot Controller → Service → MyBatis-Plus Mapper → MySQL。

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

## 7. 数据库初始化策略

- 建表与种子脚本:[`api/src/main/resources/db/schema.sql`](../../api/src/main/resources/db/schema.sql),**幂等**(`CREATE TABLE IF NOT EXISTS` + `INSERT IGNORE`)。
- 启动自动初始化:`spring.sql.init.mode=always` + `schema-locations=classpath:db/schema.sql`,应用启动时确保表与种子存在——缺表自动创建,有表为无害空操作,不丢数据。
- 测试 profile(H2)关闭自动初始化(`mode: never`),避免在 H2 上执行 MySQL 方言脚本。
- 注意:自动初始化负责"表",不负责"库";`ai_store_manage` 数据库本身需先存在。
- 该策略的背景与根因分析见 [features/department.md](../features/department.md) 的「问题分析」一节。

## 8. 开发流程规范

详见项目根 [`CLAUDE.md`](../../CLAUDE.md)。核心七条:

1. 先 Contract,后 Backend,再 Frontend;
2. 未验证不进入下一阶段;
3. 未在契约定义不允许实现;
4. 每阶段只改对应目录;
5. 输出完整、可运行、可验证;
6. 中文交流;
7. 完成功能后输出 / 更新文档(本目录)。

## 9. 文档组织

- `docs/design/` —— **总体设计**(本目录):系统级架构、技术栈、约定、流程。
- `docs/features/` —— **功能设计**:按模块拆分的数据模型、接口、设计决策与实现过程。
