# 测试指南（如何运行与编写测试）

> 三层测试:后端(`api/`)、契约(`api-contract/`)、前端(`web/`)。对应开发铁律 [CLAUDE.md](../../CLAUDE.md) 第 8 条:完成功能后必须补测试并跑通。

## 0. 一览

| 层 | 框架 | 命令 | 前置条件 |
| --- | --- | --- | --- |
| 后端 api | JUnit 5 + Spring MockMvc(真实 PostgreSQL) | `cd api && mvn test` | Docker + dev PG 容器 + `ai_store_test` 库 |
| 契约 api-contract | swagger-parser(OpenAPI 校验) | `cd api-contract && npm install && npm test` | Node |
| 前端 web | Vitest + Testing Library(jsdom) | `cd web && npm install && npm test` | Node |

---

## 1. 后端测试（api/）

### 形态
- `@SpringBootTest` + `@AutoConfigureMockMvc`,通过 **MockMvc** 走 Controller → Service → Mapper → **真实 PostgreSQL** 的完整链路。
- 每个用例标 `@Transactional`,执行后**自动回滚**,用例之间互不影响;Flyway 灌入的 8 条部门种子为已提交数据,测试可读。
- 覆盖:用户/部门/顾客的创建、查询、更新、状态、删除,以及错误分支(`400 VALIDATION_ERROR`、`404 *_NOT_FOUND`、`409 DUPLICATE_*` / `DEPARTMENT_IN_USE`)、密码不出现在响应等。
- 测试类:`AiStoreManageApplicationTests`(上下文加载)、`module/user/UserControllerTest`、`module/department/DepartmentControllerTest`、`module/customer/CustomerControllerTest`。基类 `AbstractPostgresTest`。

### 前置条件:dev PostgreSQL + 测试库
测试连接 **dev PG 容器**(`ai-store-pg`,端口 5433)内的独立库 **`ai_store_test`**(与业务库 `ai_store_manage` 隔离)。首次需创建:

```bash
# 1) 起 dev PostgreSQL 容器（若尚未创建）
docker run -d --name ai-store-pg \
  -e POSTGRES_DB=ai_store_manage \
  -e POSTGRES_USER=ai_store \
  -e POSTGRES_PASSWORD=ai_store_pwd \
  -p 5433:5432 \
  -v ai-store-pg-data:/var/lib/postgresql/data \
  postgres:16

# 2) 创建独立测试库
docker exec ai-store-pg psql -U ai_store -d postgres -c "CREATE DATABASE ai_store_test OWNER ai_store;"
```

测试库连接见 `api/src/test/resources/application-test.yml`;Flyway 启动时在该库自动建表灌种子。

### 运行
```bash
cd api
mvn test
```

### 为什么不用 Testcontainers
本仓库最初选用 Testcontainers(临时拉起 PG 容器),但本机 **Docker Engine 29** 要求最小 API 版本 **≥ 1.40**,而当前 Testcontainers 自带的 docker-java **固定使用 API 1.32**,握手时被守护进程以 `400 client version 1.32 is too old` 拒绝(`DOCKER_API_VERSION`、升级 Testcontainers 版本均未能覆盖)。因此改为连接上面这台**真实 dev PG**——测试价值等同(真 PG + 真 Flyway + 真 SQL),仅需容器运行。
> 待 Testcontainers/docker-java 支持 Docker 29 后,可切回:在 `pom.xml` 加回 `org.testcontainers:postgresql` 等依赖,`AbstractPostgresTest` 用 `@Container @ServiceConnection PostgreSQLContainer` 即可。

---

## 2. 契约测试（api-contract/）

用 `@apidevtools/swagger-parser` 对 `openapi.yaml` 做 **OpenAPI 3.0 合规校验 + `$ref` 解析**,并断言关键端点与部门类型枚举齐全(见 `validate.mjs`)。

```bash
cd api-contract
npm install   # 首次
npm test      # = node validate.mjs
```

通过时输出:`✓ openapi.yaml 校验通过：openapi=3.0.3, paths=7, schemas=16, 关键端点 8 个齐全, 部门类型 8 类齐全`。

---

## 3. 前端测试（web/）

Vitest + `@testing-library/react`(jsdom 环境),配置见 `web/vitest.config.ts`、初始化见 `web/src/test/setup.ts`。覆盖:

- `utils/format.test.ts` —— 手机号脱敏、日期格式化;
- `constants/enums.test.ts` —— 部门 8 类型 / 状态枚举;
- `api/department.test.ts` —— departmentApi 调用的方法与 URL(mock axios);
- `components/StatusTag/StatusTag.test.tsx` —— 组件渲染。

```bash
cd web
npm install   # 首次
npm test      # = vitest run
```

> 注:测试文件(`*.test.ts(x)`)已在 `tsconfig.app.json` 中排除,不参与 `npm run build` 的 `tsc` 生产构建;Vitest 用独立的 `vitest.config.ts`(与 `vite.config.ts` 分开,避免插件类型冲突)。

---

## 4. 给新功能补测试（落实 CLAUDE.md 第 8 条）

1. **后端**:在 `api/src/test/java/.../<模块>/` 加 `XxxControllerTest`,继承 `AbstractPostgresTest`,用 MockMvc 覆盖正常流 + 4xx 分支;命名以 `*Test` 结尾(Surefire 才会执行)。
2. **契约**:若新增端点,更新 `validate.mjs` 的 `REQUIRED_OPERATIONS` 列表。
3. **前端**:在被测文件旁加 `*.test.ts(x)`,覆盖工具/枚举/api/组件。
4. 三处 `npm test` / `mvn test` 全绿后,功能才算完成。
