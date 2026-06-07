# 用户管理模块（User Module）技术规格与实现说明书

本说明书整合了用户模块的业务设计、数据库结构、API 契约以及后端 Spring Boot 代码落地方案，消除了原有设计文档中的冗余信息，是一站式开发与维护的核心技术文档。

---

## 1. 概述与业务背景

### 1.1 核心实体定位
**用户（User）**是系统的核心身份实体，对应数据库表 `sys_user`。它承载了系统登录、身份认证、权限关联等基础功能，并为 WMS（仓库管理系统）业务流程（如入库、出库、审核等）提供操作人追溯。

### 1.2 适用角色与权限范围
- **超级管理员**：拥有全部 CRUD 权限、启用/禁用、重置密码等所有管理权限。
- **系统管理员**：负责本部门/本仓库用户的 CRUD 管理及启用/禁用。
- **普通用户**：允许查看个人基本信息、编辑部分个人资料（如昵称、头像、手机号等）。

---

## 2. 数据库设计 (`sys_user`)

### 2.1 核心设计决策
- **工号与主键解耦**：数据库使用自增 `id`（BIGINT）作为物理主键，不暴露业务语义；使用 `employeeNo`（VARCHAR）作为业务工号。这能避免因工号编码规则调整而影响外键关联数据。
- **逻辑删除与唯一约束**：采用逻辑删除字段 `deleted`（0=未删除，1=已删除）。为了防止已删除的用户占用工号或账号，将唯一索引设置为组合索引 `(username, deleted)` 和 `(employeeNo, deleted)`。
- **数据审计保障**：引入统一的审计字段（`createdAt`、`updatedAt`、`createdBy`、`updatedBy`），配合业务单据保存人员 ID，从而实现全链路的追溯与合规审计。

### 2.2 字段规格明细表

| 字段名 | 中文名 | 物理类型 | 长度/约束 | 必填 | 默认值 | 唯一性 | 可否编辑 | 列表展示 | 查询方式 | 校验规则与设计理由 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **id** | 主键 | BIGINT | — | 是 | 自增 | 是 | 否 | 否 | 否 | 数据库自增，不暴露业务语义 |
| **employeeNo** | 工号 | VARCHAR | 32 | 是 | — | 是 | 否 | 是 | 是(前缀/精准) | 2~32 字符，字母数字连字符；创建后不可改 |
| **username** | 登录账号 | VARCHAR | 64 | 是 | — | 是 | 否 | 是 | 是(精准) | 4~64 字符，字母数字下划线；创建后不可改 |
| **password** | 登录密码 | VARCHAR | 255 | 是 | — | 否 | 否 | 否 | 否 | 8~32 字符，含字母与数字；BCrypt 加密存储 |
| **name** | 真实姓名 | VARCHAR | 64 | 是 | — | 否 | 是 | 是 | 是(模糊) | 2~64 字符；用于审批流和单据打印 |
| **nickname** | 昵称 | VARCHAR | 64 | 否 | — | 否 | 是 | 是 | 否 | 最长 64 字符；隐私模式下的显示名称 |
| **avatar** | 头像 URL | VARCHAR | 512 | 否 | — | 否 | 是 | 否 | 否 | 合法 URL，最长 512 字符 |
| **gender** | 性别 | TINYINT | — | 否 | 0 | 否 | 是 | 是 | 是(精准) | 枚举 `[0=未知, 1=男, 2=女]`，符合 GB/T 2261.1 |
| **phoneNumber**| 手机号码 | VARCHAR | 20 | 是 | — | 否 | 是 | 是 | 是(精准) | 格式满足 `^1[3-9]\d{9}$`；脱敏控制 |
| **email** | 企业邮箱 | VARCHAR | 128 | 否 | — | 否 | 是 | 否 | 否 | 符合标准邮箱格式，最长 128 字符 |
| **jobTitle** | 职位名称 | VARCHAR | 64 | 否 | — | 否 | 是 | 是 | 是(模糊) | 最长 64 字符；MVP 阶段为自由文本 |
| **departmentId**| 部门 ID | BIGINT | — | 否 | — | 否 | 是 | 是 | 是(精准) | 外键关联部门，用于多仓/多部门权限隔离 |
| **hidePhoneNumber**| 隐藏手机 | TINYINT(1)| — | 否 | 0 | 否 | 是 | 否 | 否 | 布尔开关；控制列表/详情对非管理员脱敏 |
| **hideName** | 隐藏姓名 | TINYINT(1)| — | 否 | 0 | 否 | 是 | 否 | 否 | 布尔开关；控制是否以昵称代替真实姓名显示 |
| **remark** | 管理员备注 | VARCHAR | 500 | 否 | — | 否 | 是 | 否 | 否 | 最长 500 字符；记录特殊管理说明 |
| **status** | 账号状态 | TINYINT | — | 是 | 1 | 否 | 是 | 是 | 是(精准) | 枚举 `[0=禁用, 1=启用]`；离职/禁用不删数据 |
| **createdAt** | 创建时间 | DATETIME | — | 是 | CURRENT| 否 | 否 | 是 | 是(范围) | 系统自动生成，格式 `yyyy-MM-dd'T'HH:mm:ss` |
| **updatedAt** | 更新时间 | DATETIME | — | 是 | CURRENT| 否 | 否 | 否 | 否 | 系统自动更新，仅详情响应暴露 |
| **createdBy** | 创建人 ID | BIGINT | — | 否 | — | 否 | 否 | 否 | 否 | 记录创建者的用户主键 |
| **updatedBy** | 更新人 ID | BIGINT | — | 否 | — | 否 | 否 | 否 | 否 | 记录最后更新者的用户主键 |
| **deleted** | 逻辑删除 | TINYINT(1)| — | 是 | 0 | 否 | 否 | 否 | 否 | 0=未删除，1=已删除；参与组合唯一索引 |

### 2.3 索引设计建议
- **主键**：`PRIMARY KEY (id)`
- **唯一约束**：`UNIQUE INDEX udx_username_deleted (username, deleted)`
- **唯一约束**：`UNIQUE INDEX udx_employee_no_deleted (employeeNo, deleted)`
- **单列索引**：`INDEX idx_phone_number (phoneNumber)`
- **单列索引**：`INDEX idx_department_id (departmentId)`
- **单列索引**：`INDEX idx_status (status)`
- **前缀模糊索引**：`INDEX idx_name (name)` （支持 `LIKE 'name%'` 查询）

---

## 3. 接口契约与 API 设计 (OpenAPI 3.0.3)

### 3.1 核心 RESTful API 列表

| 请求方式 | API 路径 | operationId | 请求体 | 响应状态码 & 响应体 | 核心用途与设计考量 |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/users` | `createUser` | `CreateUserRequest` | `201` -> `User` | **创建用户**：必填工号、账号、初始密码、姓名及手机。创建成功后返回完整详情以减少二次请求。 |
| **GET** | `/api/users` | `listUsers` | — *(Query 传参)* | `200` -> `UserListResponse` | **查询列表**：支持分页与多维度组合筛选（支持 `keyword` 跨字段模糊匹配）。返回精简的 `UserSummary` 结构以优化带宽。 |
| **GET** | `/api/users/{id}` | `getUserById` | — | `200` -> `User` | **查询详情**：查询指定 ID 用户的完整信息，包括审计、备注及隐私配置。 |
| **PUT** | `/api/users/{id}` | `updateUser` | `UpdateUserRequest` | `200` -> `User` | **更新用户**：全部字段选填（非 null 覆盖）。不允许在此接口修改工号、登录账号及密码。 |
| **DELETE**| `/api/users/{id}` | `deleteUser` | — | `204` -> *(空)* | **删除用户**：物理上仅进行逻辑删除（`deleted` 标为 1），返回 204 无内容，符合标准 RESTful 规范。 |
| **PATCH** | `/api/users/{id}/status`| `updateUserStatus`| `UpdateUserStatusRequest`| `200` -> `User` | **变更状态**：专用于启用/禁用账号，防止在常规更新接口中误操作，便于后续接入状态审计。 |
| **PUT** | `/api/users/{id}/password`| `resetUserPassword`| `ResetUserPasswordRequest`| `204` -> *(空)* | **重置密码**：管理员单向重置密码，必须通过独立的高敏感安全接口进行，仅接收新密码，不返回内容。 |

### 3.2 敏感信息与隐私保护策略
> [!IMPORTANT]
> - **密码隔离**：密码字段 `password` 仅出现在 `CreateUserRequest` 和 `ResetUserPasswordRequest` 中，且声明为 `format: password`。任何查询与列表响应 Schema 均**严禁包含密码字段**。
> - **逻辑删除隐藏**：`deleted` 属于系统内部管理字段，任何前端 API 均不暴露该字段。
> - **敏感信息脱敏**：
>   - 当 `hidePhoneNumber` 为 1（开启）且请求端非管理员角色时，API 返回的 `phoneNumber` 需脱敏为 `138****8000` 格式。
>   - 当 `hideName` 为 1（开启）且请求端非管理员角色时，API 返回的 `name` 需用 `nickname` 覆盖或进行去姓氏处理。
>   - 列表接口使用 `UserSummary` 响应体，本身不包含 `remark`、隐私控制开关、多余的审计信息。

---

## 4. DTO 与 VO 设计规范

### 4.1 请求 DTO (Request Body)
- **CreateUserRequest**：
  - 必须字段：`employeeNo`（长度2~32，支持字母/数字/连字符）、`username`（长度4~64，支持字母/数字/下划线）、`password`（长度8~32，必须含字母与数字）、`name`（长度2~64）、`phoneNumber`（正则校验 `^1[3-9]\d{9}$`）。
- **UpdateUserRequest**：
  - 所有字段均为选填。不可传入 `employeeNo`、`username`、`password`。
- **UpdateUserStatusRequest**：
  - 必须字段：`status`（值必须为 0 或 1）。
- **ResetUserPasswordRequest**：
  - 必须字段：`newPassword`（长度8~32，必须含字母与数字）。
- **UserQueryParam**：
  - 列表查询封装，包含分页参数 `page`（起始为 1，默认 1）和 `pageSize`（默认 20），以及各筛选条件的可选入参。

### 4.2 响应 VO (Response Body)
- **UserVO** (`User` Schema)：
  - 完整信息响应，包含 `departmentName`（通过部门 ID 关联查询得到，为后续部门模块预留）。不含密码和 `deleted` 标记。
- **UserSummaryVO** (`UserSummary` Schema)：
  - 列表专用响应体。去除了大字段 `remark`、隐私配置字段（`hideName`、`hidePhoneNumber`）及部分审计字段（如创建人、更新人），以提升列表查询吞吐率。
- **UserListResponse**：
  - 包裹分页数据的响应体：`items` (List<UserSummaryVO>)、`total`（总记录数）、`page`（当前页码）、`pageSize`（每页大小）、`totalPages`（总页数）。
- **ErrorResponse** & **FieldError**：
  - 统一错误返回格式。`ErrorResponse` 包含 `code`（错误码，如 `DUPLICATE_USERNAME`）、`message`（描述信息）以及 `details`（字段级校验失败的 `FieldError` 列表，包含 `field` 与 `message` ）。

---

## 5. 后端落地实现方案 (Spring Boot 3.4.5 + Java 21)

### 5.1 依赖选型与修正
> [!IMPORTANT]
> 原项目 `pom.xml` 中将 Java 版本误配置为了 `23`，本次需统一修正为官方约定的 **Java 21**。
> 引入的核心技术栈依赖：
> - **MySQL Connector J**：数据库驱动
> - **MyBatis-Plus Starter** (适配 Spring Boot 3)：用于快速 CRUD、分页及逻辑删除
> - **Spring Boot Starter Validation**：用于声明式 JSR-380 参数校验
> - **Spring Boot Starter Security**：引入 BCryptPasswordEncoder 用于密码哈希，并在安全配置中放行全部端点以禁用默认的安全过滤链（MVP 阶段暂不引入复杂的认证授权）
> - **Lombok**：消除 Java 样板代码

### 5.2 后端包结构设计 (`com.aistore`)
```
com.aistore
├── AiStoreManageApplication.java          # 启动类
├── config/                                 # 全局配置类
│   ├── MyBatisPlusConfig.java             # MyBatis-Plus 分页插件配置
│   ├── MyBatisMetaObjectHandler.java      # 自动填充 createdAt/updatedAt 处理器
│   ├── WebConfig.java                     # Jackson LocalDateTime 格式化 (ISO 格式)
│   └── SecurityConfig.java                # BCrypt Bean 声明及全局接口放行
├── common/                                 # 公共组件包
│   ├── exception/
│   │   ├── BusinessException.java         # 业务异常基类
│   │   ├── ResourceNotFoundException.java # 资源未找到异常 (映射 404)
│   │   ├── DuplicateResourceException.java# 资源冲突异常 (映射 409)
│   │   └── GlobalExceptionHandler.java    # @RestControllerAdvice 异常捕获与对齐响应
│   └── response/
│       ├── ErrorResponse.java             # 错误响应实体
│       └── FieldError.java                # 属性字段校验失败项
└── module/
    └── user/                               # 用户独立业务模块
        ├── controller/
        │   └── UserController.java        # REST 接口端点实现
        ├── dto/                           # 请求 DTO 接收类
        │   ├── CreateUserRequest.java
        │   ├── UpdateUserRequest.java
        │   ├── UpdateUserStatusRequest.java
        │   ├── ResetUserPasswordRequest.java
        │   └── UserQueryParam.java
        ├── vo/                            # 响应 VO 返回类
        │   ├── UserVO.java
        │   ├── UserSummaryVO.java
        │   └── UserListResponse.java
        ├── entity/
        │   └── SysUser.java               # 数据库实体（搭配 MyBatis-Plus 注解）
        ├── mapper/
        │   └── SysUserMapper.java         # Mapper 数据库接口
        ├── service/
        │   ├── UserService.java           # 用户业务接口
        │   └── UserServiceImpl.java       # 用户业务逻辑实现
        └── converter/
            └── UserConverter.java         # 模型互转工具类
```

### 5.3 核心组件实现细节
1. **自动填充**：在 `SysUser` 实体类上配置 `@TableField(fill = FieldFill.INSERT)` 标记 `createdAt`，配置 `@TableField(fill = FieldFill.INSERT_UPDATE)` 标记 `updatedAt`。通过注册 `MyBatisMetaObjectHandler` 在插入与更新时利用代码自动填充时间戳，无需在业务中手动赋值。
2. **逻辑删除**：在 `SysUser` 实体的 `deleted` 字段上添加 `@TableLogic` 注解。在调用 MyBatis-Plus 的 `deleteById` 时，底层自动执行 `UPDATE sys_user SET deleted = 1 WHERE id = ?`；在执行查询时，自动拼接 `AND deleted = 0` 过滤逻辑。
3. **接口转换**：创建专职的 `UserConverter` 模块。避免直接向表现层暴露 `SysUser` 实体。同时支持在更新操作中对非 null 的修改字段进行差异化属性覆盖。
4. **分页页码对齐**：API 契约约定分页索引从 1 开始（符合前端及用户直觉），而 MyBatis-Plus 的 Page 分页对象默认也是 1-based，两者天然兼容。注意：如果底层的传统框架或 Spring Data 改用 0-based 分页，需在 Service 层做转换（即 `page - 1`）。
5. **全局异常处理**：
   - 捕捉 `MethodArgumentNotValidException`（校验失败）映射为 HTTP 400，并解析各个属性校验错误填充至 `ErrorResponse.details` 中。
   - 捕捉 `ResourceNotFoundException`（资源未找到）映射为 HTTP 404，返回包含未找到原因的 `ErrorResponse`。
   - 捕捉 `DuplicateResourceException`（工号/账号重复冲突）映射为 HTTP 409，返回对应的错误码（如 `DUPLICATE_USERNAME`）。

---

## 6. 验证与测试手册

### 6.1 编译与运行
- **本地编译验证**
  ```bash
  cd api && ./mvnw clean compile
  ```
- **启动应用** (确保本地 MySQL 启动并建表)
  ```bash
  cd api && ./mvnw spring-boot:run
  ```

### 6.2 数据库初始化
```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_store_manage CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE ai_store_manage;

-- 用户表结构定义
CREATE TABLE sys_user (
  id BIGINT AUTO_INCREMENT COMMENT '自增主键',
  employee_no VARCHAR(32) NOT NULL COMMENT '工号',
  username VARCHAR(64) NOT NULL COMMENT '登录账号',
  password VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
  name VARCHAR(64) NOT NULL COMMENT '姓名',
  nickname VARCHAR(64) DEFAULT NULL COMMENT '昵称',
  avatar VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
  gender TINYINT DEFAULT 0 NOT NULL COMMENT '性别: 0=未知, 1=男, 2=女',
  phone_number VARCHAR(20) NOT NULL COMMENT '手机号码',
  email VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  job_title VARCHAR(64) DEFAULT NULL COMMENT '职位',
  department_id BIGINT DEFAULT NULL COMMENT '所属部门ID',
  hide_phone_number TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否隐藏手机号: 0=不隐藏, 1=隐藏',
  hide_name TINYINT(1) DEFAULT 0 NOT NULL COMMENT '是否隐藏姓名: 0=不隐藏, 1=隐藏',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  status TINYINT DEFAULT 1 NOT NULL COMMENT '状态: 0=禁用, 1=启用',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  updated_at DATETIME NOT NULL COMMENT '更新时间',
  created_by BIGINT DEFAULT NULL COMMENT '创建人',
  updated_by BIGINT DEFAULT NULL COMMENT '更新人',
  deleted TINYINT(1) DEFAULT 0 NOT NULL COMMENT '逻辑删除标记: 0=未删除, 1=已删除',
  PRIMARY KEY (id),
  UNIQUE KEY udx_username_deleted (username, deleted),
  UNIQUE KEY udx_employee_no_deleted (employee_no, deleted),
  KEY idx_phone_number (phone_number),
  KEY idx_department_id (department_id),
  KEY idx_status (status),
  KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户基础信息表';
```

### 6.3 核心 API 连通性测试命令 (curl)

以下是一套用于验证用户接口行为的 curl 脚本：

```bash
# 1. 创建新用户 (期望: 201 Created，返回包含主键ID的详情VO)
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{
    "employeeNo": "WH-20260001",
    "username": "zhangsan",
    "password": "Password1234",
    "name": "张三",
    "phoneNumber": "13800138000"
  }'

# 2. 账号唯一性冲突测试 (期望: 409 Conflict，返回 ERROR_CODE="DUPLICATE_USERNAME" 的 ErrorResponse)
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{
    "employeeNo": "WH-20260002",
    "username": "zhangsan",
    "password": "Password1234",
    "name": "张三二号",
    "phoneNumber": "13800138001"
  }'

# 3. 参数校验缺失测试 (期望: 400 Bad Request，返回 ErrorResponse 且 details 包含各字段具体错误说明)
curl -X POST http://localhost:8080/api/users \
  -H 'Content-Type: application/json' \
  -d '{}'

# 4. 获取用户列表 (期望: 200 OK，包含分页信息及 UserSummary 列表)
curl 'http://localhost:8080/api/users?page=1&pageSize=10&status=1'

# 5. 获取指定用户详情 (假设刚才创建的主键 ID 为 1，期望: 200 OK，包含完整审计和隐藏设置)
curl http://localhost:8080/api/users/1

# 6. 更新用户信息 (期望: 200 OK，仅更新传入的字段)
curl -X PUT http://localhost:8080/api/users/1 \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "张三丰",
    "jobTitle": "高级仓管员",
    "nickname": "三哥"
  }'

# 7. 变更用户状态 (禁用用户。期望: 200 OK，status 更新为 0)
curl -X PATCH http://localhost:8080/api/users/1/status \
  -H 'Content-Type: application/json' \
  -d '{"status": 0}'

# 8. 重置密码 (期望: 204 No Content，无响应体)
curl -X PUT http://localhost:8080/api/users/1/password \
  -H 'Content-Type: application/json' \
  -d '{"newPassword": "NewPassword5678"}'

# 9. 逻辑删除用户 (期望: 204 No Content，后续查询该 ID 返回 404)
curl -X DELETE http://localhost:8080/api/users/1

# 10. 验证逻辑删除后的 404 (期望: 404 Not Found)
curl http://localhost:8080/api/users/1
```
