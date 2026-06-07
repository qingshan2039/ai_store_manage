# 运输管理模块（车辆 / 打油记录 / 司机打卡）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

管理自有车队：登记**车辆**并为每辆车指定**常态化班组（常态司机 + 常态跟车员）**，记录**打油（加油）流水（含小票照片）**，并**跟踪司机每日打卡**（含当天司机/跟车员，缺席可填替补）。

关键建模决策（经与需求方两轮确认）：

- **司机/跟车员 = `sys_user`**：不另建花名册表，不单独建管理页，统一在「系统管理 → 用户管理」维护。按部门类型区分用途：**司机 = 运输部 TRANSPORT 用户；跟车员 = 仓库 WAREHOUSE / 生产 PRODUCTION 用户**。
- **新表只有 3 张**：`vehicle`、`fuel_record`、`driver_checkin`。
- **"用户 或 OTHER 替补"**：车辆常态班组与每日打卡的司机/跟车员，均建模为「软引用 `sys_user`（`*_user_id`）**或** OTHER 文本替补（`*_other`）」二选一；OTHER 用于当事人缺席时登记临时替补。
- **打油支持多图**：`fuel_record.images` 为 jsonb 字符串数组；图片经通用上传接口 `POST /api/files`（本地磁盘存储 + 静态资源映射）落地并回填 URL。

种子数据：4 辆车（车牌 9924 / 6115 / 7744 / 7601）、4 名司机（运输部）、4 名跟车员（仓库/生产），并预置最近 7 天的打卡历史（含迟到/缺勤/请假与一条跟车员替补，演示数据多样性）。

## 2. 需求 → 设计落点

| 需求 | 落点 |
| --- | --- |
| 4 辆车（9924/6115/7744/7601） | `vehicle` 表 + V6 种子 4 行（`plate_no` 唯一） |
| 随机 4 名司机 / 4 名跟车员 | 种入运输部 / 仓库·生产部 `sys_user`（job_title=司机/跟车员） |
| 车辆常态化司机 + 跟车员添加 | `vehicle.default_driver_*` / `default_escort_*`（用户或 OTHER 对） |
| 打油管理 + 上传图片 | `fuel_record`（`images` jsonb）+ 通用 `POST /api/files` 上传 + `/api/files/**` 静态映射 |
| 跟踪每天司机打卡 | `driver_checkin`（`(driver_user_id, checkin_date)` 唯一）+ 最近 7 天种子 |
| OTHER + remark（缺席替补） | 车辆/打卡的司机、跟车员均用 `*_user_id` 或 `*_other` 建模；记录级 `remark` 记原因 |
| 按部门挑司机/跟车员 | `GET /api/users` 增量加 `departmentType` 过滤参数 |

## 3. 数据模型（PostgreSQL，迁移 [`V8__add_vehicle_fuel_checkin.sql`](../../api/src/main/resources/db/migration/V8__add_vehicle_fuel_checkin.sql)）

软引用统一采用 **BIGINT + 索引、不建 DB 外键**（与 `sku.spu_id` 一致），关联名靠 Service 层 `selectBatchIds` 回填。

### 3.1 `vehicle`（车辆）
`plate_no`(uk，带 deleted) / `default_driver_user_id` / `default_driver_other` / `default_escort_user_id` / `default_escort_other` / `remark` / `status` / 审计 / `deleted`。四个班组字段标注 `@TableField(updateStrategy=IGNORED)`，使更新时可写 null（支持 用户 ↔ OTHER 切换/清空）。索引 `idx_vehicle_status`、`idx_vehicle_default_driver`。

### 3.2 `fuel_record`（打油记录）
`vehicle_id` / `driver_user_id` / `fuel_date`(DATE) / `liters` `amount` `unit_price` `odometer`(NUMERIC) / **`images`(JSONB)** / `remark` / 审计 / `deleted`。索引 `idx_fuel_vehicle`、`idx_fuel_date`。

### 3.3 `driver_checkin`（司机每日打卡）
`driver_user_id` / `driver_other` / `vehicle_id` / `escort_user_id` / `escort_other` / `checkin_date`(DATE) / `clock_in_at` `clock_out_at`(TIMESTAMP) / `checkin_status`(VARCHAR，存枚举名) / `remark` / 审计 / `deleted`。`UNIQUE(driver_user_id, checkin_date, deleted)`——在册司机同日唯一；OTHER 替补 `driver_user_id` 为 NULL，PG 视 NULL 互异故不受限。索引 `idx_checkin_driver`、`idx_checkin_date`。

## 4. 接口契约

契约源 [`api-contract/openapi.yaml`](../../api-contract/openapi.yaml)。

| 资源 | 路径 | 端点 | 关键查询参数 |
| --- | --- | --- | --- |
| 车辆 | `/api/vehicles` | 6 端点（CRUD + `{id}/status`） | keyword、status |
| 打油记录 | `/api/fuel-records` | 5 端点（CRUD，**无**状态切换） | vehicleId、fuelDateStart/End |
| 司机打卡 | `/api/driver-checkins` | 5 端点（CRUD，**无**状态切换） | driverUserId、vehicleId、checkinStatus、checkinDateStart/End |
| 文件上传 | `/api/files` | POST（multipart，字段 `file`）→ 201 `{ url }` | — |
| 用户（改造） | `/api/users` | 列表新增 `departmentType` 过滤参数 | departmentType |

枚举 `CheckinStatus`：`NORMAL/LATE/ABSENT/LEAVE`（正常/迟到/缺勤/请假）。错误码：`DUPLICATE_VEHICLE_PLATE`(409)、`DUPLICATE_DRIVER_CHECKIN`(同司机同日 409)、`DRIVER_REQUIRED`(打卡缺司机 400)、`VEHICLE_NOT_FOUND`/`FUEL_RECORD_NOT_FOUND`/`DRIVER_CHECKIN_NOT_FOUND`(404)、`FILE_EMPTY`/`FILE_TYPE_NOT_ALLOWED`/`FILE_TOO_LARGE`(400)、`VALIDATION_ERROR`(400)。

## 5. 关键设计决策

- **司机/跟车员复用 `sys_user`**：避免重复花名册；按部门类型（TRANSPORT / WAREHOUSE+PRODUCTION）过滤候选人，前端选人下拉据此拉取。
- **"用户 或 OTHER" 多态引用**：`*_user_id`(软引用) 与 `*_other`(文本) 成对，覆盖车辆常态配置与每日打卡；更新时这些字段用 `FieldStrategy.IGNORED` 整体覆盖（含写 null），以支持 用户 ↔ OTHER 的切换/清空。
- **打油图片用 jsonb 数组**：实体 `List<String>` + `@TableName(autoResultMap=true)` + 自定义 [`JsonbStringListTypeHandler`](../../api/src/main/java/com/aistore/common/handler/JsonbStringListTypeHandler.java)；列表/详情走 `BaseMapper.selectPage/selectById` 使类型处理器生效（故打油模块无自定义 XML）。
- **通用本地上传**：[`FileStorageService`](../../api/src/main/java/com/aistore/common/storage/FileStorageService.java) 存到 `${app.upload.dir}/yyyy/MM/{uuid}.ext`（UUID 重命名、限图片类型与大小），[`WebConfig`](../../api/src/main/java/com/aistore/config/WebConfig.java) 将 `/api/files/**` 映射到该目录对外访问；后续如需对象存储替换此类即可。
- **打卡同司机同日唯一**：在册司机校验唯一（命中 409）；OTHER 替补不限制。司机必填（在册或替补其一），否则 400。
- **关联名回填**：Service 层批量 `selectBatchIds` 解析司机/跟车员姓名、车牌（显示名 = 在册用户名 ?? 替补名），避免 N+1。

## 6. 实现与验证

- 后端：新增 `module/{vehicle,fuel,checkin}` 三模块全分层 + `common/storage/FileStorageService` + `controller/FileUploadController` + `JsonbStringListTypeHandler` + V6 迁移与种子；改造 `WebConfig`（静态映射）、`application.yml`（multipart/upload.dir）、用户模块（`departmentType` 过滤）。编译通过，V6 自动迁移。
- 实机冒烟（备用端口 + 干净库）：`GET /api/vehicles` 见 4 车带常态班组名；`GET /api/users?departmentType=TRANSPORT` 见 4 司机；`GET /api/driver-checkins` 见 28 条（4 司机 × 7 天）按日倒序；`POST /api/files` 传图返回 URL → `GET` 该 URL 回显 200 + image/png；非图片上传 400。
- 前端：新增「运输管理」菜单（车辆 / 打油记录 / 司机打卡 三页），复用 `useTable`/`useModal`；新增 [`StaffPicker`](../../web/src/components/StaffPicker/index.tsx)（用户或 OTHER 替补选择器）、[`ImageUpload`](../../web/src/components/ImageUpload/index.tsx)（多图上传）、[`useCrewOptions`](../../web/src/hooks/useCrewOptions.ts)（拉司机/跟车员候选）。`tsc -b && vite build` 通过。

## 7. 测试

- 后端（JUnit + MockMvc，真实 PostgreSQL）：`VehicleControllerTest`(8)、`FuelRecordControllerTest`(5)、`DriverCheckinControllerTest`(7)、`FileUploadControllerTest`(3)，并在 `UserControllerTest` 增 `departmentType` 过滤用例。覆盖 201/200/404/400(校验/缺司机)/409(车牌重复·同司机同日)/204、jsonb 多图回读、OTHER 替补、用户或 OTHER 切换清空。全后端 **99** 用例全绿。
- 前端（Vitest）：`api/{vehicle,fuelRecord,driverCheckin,file}.test.ts`（端点 URL/参数、上传 FormData + Content-Type 置 null）、`enums.test.ts` 增 `CheckinStatus`。前端 **82** 用例全绿。
- 契约（swagger-parser）：`validate.mjs` 断言三资源 + 文件上传关键端点、`CheckinStatus` 枚举、`GET /api/users` 含 `departmentType` 参数。

> ⚠️ 迁移版本号：本模块迁移已改号为 **V8**——合并时 main 已含兄弟分支的 V6（包装条码）、V7（库存 LPN），故本模块顺延到 V8，避免版本号冲突。测试连真实 PostgreSQL；若共享测试库 `ai_store_test` 的 Flyway 历史与本地迁移校验和不符（多分支共享所致），用干净库重跑：`SPRING_DATASOURCE_URL='jdbc:postgresql://localhost:5433/<干净库>' mvn test`。

## 8. 演进记录

- v1：运输管理 `vehicle` / `fuel_record` / `driver_checkin`（迁移 V8，接在 main 既有 V6 包装条码 / V7 库存 LPN 之后）。司机/跟车员复用 `sys_user`；"用户或 OTHER 替补"多态引用；打油图片 jsonb + 通用本地上传；用户列表加 `departmentType` 过滤。
- 后续可选：打油记录司机也支持 OTHER 替补；车队成本/油耗统计；按日期范围的打卡报表；上传改对象存储（S3/OSS）。
