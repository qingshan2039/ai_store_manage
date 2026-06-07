# 部门模块（Department Module）功能设计与实现说明

> 功能设计 + 完整实现过程 + 数据库初始化问题分析。总体设计见 [../design/overview.md](../design/overview.md)。

## 1. 概述

部门是组织的基础实体,被用户(`sys_user.department_id`)引用。采用**扁平结构**(无上下级树形),通过 `type` 字段对部门分类。

8 类 = 7 个业务部门 + 管理层:

| type | 含义 |
| --- | --- |
| WAREHOUSE | 仓管 |
| TRANSPORT | 运输 |
| SALES | 销售 |
| PRODUCTION | 生产 |
| OFFICE | 办公 |
| HR | 人事 |
| FINANCE | 财务 |
| MANAGEMENT | 管理(管理层 / 老板层级) |

> 设计说明:`MANAGEMENT` 表示领导层单位(如董事会 / 总经办),与 7 个业务部门并列于同一张扁平表,以 `type` 区分,不做树形上下级。

## 2. 数据模型 `sys_department`

| 字段 | 类型 | 约束 | 说明 |
| --- | --- | --- | --- |
| id | BIGSERIAL | PK,自增 | 主键 |
| name | VARCHAR(64) | 必填,唯一(uk_dept_name_deleted) | 部门名称 |
| code | VARCHAR(32) | 必填,唯一(uk_dept_code_deleted),创建后不可改 | 部门编码 |
| type | VARCHAR(32) | 必填 | 部门类型(枚举名) |
| status | SMALLINT | 默认 1 | 0=禁用,1=启用 |
| sort | INTEGER | 默认 0 | 显示排序(升序) |
| remark | VARCHAR(500) | 可空 | 备注 |
| created_at / updated_at | TIMESTAMP | 自动填充 | 审计 |
| created_by / updated_by | BIGINT | 可空 | 审计 |
| deleted | SMALLINT | 默认 0,`@TableLogic` | 逻辑删除 |

唯一索引含 `deleted`,逻辑删除后可复用 name / code。

## 3. 接口契约(6 个端点)

契约源:[`api-contract/openapi.yaml`](../../api-contract/openapi.yaml)。

| 方法 | 路径 | operationId | 成功码 |
| --- | --- | --- | --- |
| POST | /api/departments | createDepartment | 201 |
| GET | /api/departments | listDepartments | 200 |
| GET | /api/departments/{id} | getDepartmentById | 200 |
| PUT | /api/departments/{id} | updateDepartment | 200 |
| DELETE | /api/departments/{id} | deleteDepartment | 204 |
| PATCH | /api/departments/{id}/status | updateDepartmentStatus | 200 |

错误码:`DUPLICATE_DEPARTMENT_NAME` / `DUPLICATE_DEPARTMENT_CODE`(409)、`DEPARTMENT_NOT_FOUND`(404)、`DEPARTMENT_IN_USE`(409,删除时仍有用户引用)、`VALIDATION_ERROR`(400)。

## 4. 关键设计决策

- `name`、`code` 均全局唯一;`code` 创建后不可修改(对齐用户模块 employeeNo / username 的不可变业务键)。
- 状态变更走独立 `PATCH .../status` 接口,避免普通更新误改。
- 删除为逻辑删除;删除前校验该部门下是否仍有用户,有则 `409 DEPARTMENT_IN_USE`(跨模块查询 `sys_user`)。
- `type` 在实体以字符串(枚举名)存储,DTO / VO 用 `DepartmentType` 枚举,Converter 桥接,规避 MyBatis-Plus 枚举处理器的额外配置。
- 前端 `type` 以枚举码传输,用 `DEPARTMENT_TYPE_MAP` 映射中文标签(与用户模块 enums 约定一致)。

## 5. 实现过程（Contract → Backend → Frontend）

严格按 [`CLAUDE.md`](../../CLAUDE.md) 的阶段顺序,每阶段验证通过后再进入下一阶段。

### 阶段一:Contract（只改 `api-contract/`）
- 在 `openapi.yaml` 增加部门 tag、6 个 path、7 个 schema(`DepartmentType` 等),复用统一 `ErrorResponse`。
- 验证:YAML 语法、全部 `$ref` 可解析、operationId 齐全。

### 阶段二:Backend（只改 `api/`）
- 镜像用户模块:`module/department/`(entity / dto / vo / mapper + xml / converter / service / controller / enums)+ `ResourceInUseException` + `schema.sql` 建表与种子。
- 验证:`mvn compile`、`contextLoads`、对真实 MySQL 的 12 项端到端 curl(覆盖 201/200/204 与 409/404/400 各分支)。

### 阶段三:Frontend（只改 `web/`）
- 镜像用户模块:`types/department.ts`、`api/department.ts`、`pages/department/`(列表页 + 新增/编辑弹窗 + 搜索表单),并接入菜单 / 路由 / 面包屑 / enums。
- 验证:`tsc -b && vite build` 通过;经 vite 代理(:3000 → :8080)真实拉到后端数据。

## 6. 数据库初始化（Flyway）

数据库为 **PostgreSQL 16**(本项目由 MySQL 迁移而来;下方 §7 为 MySQL 时期的历史复盘)。部门建表与种子已纳入 Flyway 基线迁移 `api/src/main/resources/db/migration/V1__init_schema.sql`(PostgreSQL 方言),应用启动时自动迁移(空库执行 V1,已有库走 baseline)。详见总体设计「[数据库初始化与迁移](../design/overview.md)」。

## 7. 问题分析:任务结束后未自动创建 SQL 表

### 现象
部门功能(契约 + 后端 + 前端)实现并合并后,在**全新环境**(新 MySQL / 队友拉代码 / CI / 生产)中,`sys_department` 表并不存在,调用部门接口会因缺表而 500。

### 根因
1. **"写 SQL ≠ 建表"**:任务里"创建的 SQL"只是把 DDL 写进了 `schema.sql` 文件;文件本身不会在任何数据库里建表。
2. **脚本不自动执行**:`schema.sql` 位于 `classpath:db/`,并非 Spring Boot 默认自动执行的位置;且原脚本是破坏性的 `DROP TABLE + CREATE`,本就不能安全地挂到每次启动。
3. **缺少迁移机制**:项目没有 Flyway / Liquibase 之类的版本化迁移工具,建表完全依赖"人工手动执行脚本"这一隐性步骤。
4. **验证掩盖了缺口**:后端阶段是在"手动建好表"的库上做的 curl 验证(甚至一度因 mysql 客户端字符集把中文种子写成乱码),所以"功能在我的库上能跑"并不代表"全新环境也能跑"。
5. **"功能完成"的定义偏窄**:把"代码编译通过 + 接口在已备好的库上可用"当成了完成,而没有包含"数据库可被可重复地自动初始化"。

### 修复
- 将 `schema.sql` 改为**幂等**:`CREATE TABLE IF NOT EXISTS` + `INSERT IGNORE`。
- 开启 `spring.sql.init.mode=always`(测试 profile 关闭),启动时自动执行:缺表自动建表 + 灌种子,有表为无害空操作,不丢数据。
- 验证:手动 `DROP TABLE sys_department` 后启动应用 → 表被自动重建、8 条种子、中文正确、`total=8`,且 `sys_user` 未受影响;重启幂等(仍 8 行,无重复、无报错);测试 `contextLoads` 仍通过。
- 后续升级:该自动初始化已进一步演进为 **Flyway 版本化迁移**(见 §6 与总体设计),`schema.sql` 已被 `db/migration/V1__init_schema.sql` 取代。

### 经验
- "功能完成"应包含"数据库 schema 可被自动 / 可重复初始化",而非依赖隐性人工步骤。
- 验证应覆盖"全新环境(空库启动)"路径,而不仅是"我本地已备好的库"。
- 文档须记录 DB 初始化方式(已在本目录与总体设计中记录)。
- ✅ 已落实:引入 **Flyway** 版本化迁移(`db/migration/`)统一管理 schema,取代启动跑 `schema.sql`;后续变更走递增 `V2+` 迁移。

## 8. 验证记录(摘要)

- **后端 curl**:创建 201;详情/更新/状态 200;删除 204;删后 404;名称/编码重复 409;缺字段 400(3 字段);非法枚举 400;按类型筛选 total=1;被占用删除 409 `DEPARTMENT_IN_USE`。
- **前端**:`tsc -b && vite build` 通过;vite 代理实拉 `total=8`,中文正确。
- **自动建表**:`DROP` 后启动自动重建 8 条种子;重启幂等。
- **Flyway**:已有库启动 → 建立基线(`flyway_schema_history` BASELINE 行),不动数据;全新空库启动 → 执行 `V1` 自动建表 + 8 条种子(`total=8`)。
- **PostgreSQL 迁移**:应用与全部测试已在真实 PostgreSQL 16 上验证通过(BIGSERIAL 主键、SMALLINT/BOOLEAN/TIMESTAMP 映射、逻辑删除、POSTGRE_SQL 分页)。

## 9. 测试

- **后端**:`module/department/DepartmentControllerTest`(MockMvc + 真实 PostgreSQL),覆盖部门 CRUD 与 400/404/409 各分支,共 10 个用例;`cd api && mvn test`。
- **契约**:`api-contract` 的 OpenAPI 校验断言部门 6 端点 + 8 类型齐全。
- **前端**:`api/department.test.ts`(departmentApi 端点映射)、`constants/enums.test.ts`(8 类型 / 状态枚举)等;`cd web && npm test`。
- 运行方式与前置条件见 [测试指南](../design/testing.md)。
