# 物料目录模块（Material Catalog：品类 / SPU / SKU）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

原材料管理（纸皮 / 铝箔 / 纸管 / 保鲜膜 / 烘焙纸…）。完整主数据按 ER 图共 12 张表（目录 → 包装/条码 → 库存/托盘），存在硬依赖，**按阶段推进**：

- **Phase A（本模块，已实现）= 物料目录**：`material_category` + `spu` + `sku`，解决"有哪些料、什么规格、什么阶段"。
- Phase B（待做）= 包装与条码：`packaging_level` / `packaging_relation`（每托 500/480、12/16/18）/ `barcode` / `unit_conversion` / `item_image`。
- Phase C（待做）= 库存与托盘：`location` / `lpn` / `inventory`，实现"库存数量 / 托盘数量 / 整托尾托"统计。

`warehouse`、`pallet_type` 已在上一阶段落地，可复用。

## 2. 三个原始需求 → 设计落点

| 需求 | 落点 |
| --- | --- |
| **① 同尺寸还有不同规格（SKU 比尺寸更细）** | `spu`=品类层（如"3寸纸管"），`sku`=完整组合：结构化尺寸列 `length_mm/width_mm/thickness_mm/roll_length_m` + `color`/`gsm` + **`spec`(jsonb)** 存材质/牌号/工艺。纸管 340×480×5mm 下 A/B = 两个 SKU（`PC-340480-A`/`PC-340480-B`）。 |
| **② 既统计库存数量，又统计托盘数量** | 属 **Phase C**：库存以基本单位记账 `inventory.qty_on_hand`，托盘以实体 `lpn` 计数；不满托（480/500、12/16/18 roll）靠 `is_fixed_qty` + 每托标准数判整托/尾托。本阶段先把 `base_unit`（SPU）与每托标准数据所需的 SKU 维度备好。 |
| **③ SKU 加阶段类型** | `sku.item_type`（RAW/SEMI/FINISHED 原料/半成品/成品）放 **SKU 级**（同规格可分别作为半成品与成品两个 SKU）；`spu.category_code`=品类，两者正交。 |

## 3. 数据模型（PostgreSQL，迁移 [`V5__add_material_catalog.sql`](../../api/src/main/resources/db/migration/V5__add_material_catalog.sql)）

### 3.1 `material_category`（品类，含 5 条种子）
| 字段 | 类型 | 约束 |
| --- | --- | --- |
| id / code / name | BIGSERIAL / VARCHAR(32) / VARCHAR(64) | code、name 各自唯一(带 deleted) |
| sort_order | INTEGER | 默认 0，列表升序 |
| status / 审计 / deleted | SMALLINT … | 同统一约定 |

种子（迁移内置）：`PAPER 纸皮`、`FOIL 铝箔`、`CORE 纸管`、`FILM 保鲜膜`、`BAKING 烘焙纸`。

### 3.2 `spu`（标准产品单元，品类层）
`spu_code`(uk) / `spu_name`(uk) / `category_code`(引用品类) / `brand` / `base_unit`(基本单位 PCS/ROLL/张…) / status / 审计 / deleted。索引 `idx_spu_category`。

### 3.3 `sku`（最小库存单元）
`spu_id`(FK) / `sku_code`(uk) / `sku_name` / `item_type`(存枚举名) / `length_mm` `width_mm` `thickness_mm` `roll_length_m` `gsm`(NUMERIC) / `color` / **`spec`(JSONB)** / status / 审计 / deleted。索引 `idx_sku_spu`、`idx_sku_item_type`。

## 4. 接口契约（3 模块 × 6 端点）

契约源 [`api-contract/openapi.yaml`](../../api-contract/openapi.yaml)。

| 资源 | 路径 | 关键查询参数 |
| --- | --- | --- |
| 品类 | `/api/material-categories` | keyword、status |
| SPU | `/api/spus` | keyword、categoryCode、status |
| SKU | `/api/skus` | keyword、spuId、itemType、status |

每个资源 6 端点（POST 创建 201、GET 列表 200、GET/PUT/DELETE `{id}`、PATCH `{id}/status`）。`ItemType` 枚举 `RAW/SEMI/FINISHED`。错误码：`DUPLICATE_*_NAME/CODE`(409)、`*_NOT_FOUND`(404)、`MATERIAL_CATEGORY_NOT_FOUND`（SPU 引用不存在品类）、`SPU_NOT_FOUND`（SKU 引用不存在 SPU）、`VALIDATION_ERROR`(400)。

## 5. 关键设计决策

- **SKU 粒度 > 尺寸**：尺寸用结构化列、同尺寸细分用 `spec`(jsonb)；`sku_code` 编全维度。需求 1 自然成立。
- **`item_type` 在 SKU 级**：同规格的半成品/成品各为独立 SKU（注：跨阶段加工追溯需后续 BOM/工序表，本阶段先留口子）。
- **`spec` 用 PostgreSQL jsonb**：实体字段 `Map<String,Object>` + `@TableName(autoResultMap=true)` + 自定义 [`JsonbTypeHandler`](../../api/src/main/java/com/aistore/common/handler/JsonbTypeHandler.java)（写入 `setObject(.., Types.OTHER)` 让驱动按 jsonb 隐式转换，读取 Jackson 反序列化；不改全局 JDBC 配置）。列表查询不取 spec（Summary 无需）。
- **品类用表 + 种子**：品类可增长，做成可管理的表（5 条种子写进迁移，便于全新部署与测试），非硬枚举。
- **关联名回填**：SPU 回填 `categoryName`、SKU 回填 `spuCode/spuName`（批量 `selectBatchIds`/`in` 查询避免 N+1）。
- **引用完整性在应用层校验**：SPU 创建/改品类校验 `category_code` 存在；SKU 创建校验 `spu_id` 存在。

## 6. 实现与验证

- 后端 3 模块各 13 文件 + V5 迁移 + `ItemType` 枚举 + `JsonbTypeHandler`；编译通过，V5 自动迁移到 v5。
- 端到端（真实 PostgreSQL）：创建 SPU（categoryName=纸管）、坏品类→404、SKU 带 jsonb spec 创建并回读、同尺寸 A/B 两 SKU、按 spuId 过滤列表均通过。
- 前端 3 模块（types/api/列表/表单/搜索）+ 新「物料管理」菜单；SKU 表单含 SPU 下拉、阶段下拉、结构化尺寸输入、**spec 动态键值编辑器**。浏览器实测：品类 5、SPU 1、SKU 2（同尺寸 A/B、阶段 Tag、SPU 名回填）。`tsc -b && vite build` 通过。
- 种子脚本 [`scripts/seed-master-data.mjs`](../../scripts/seed-master-data.mjs) 追加 5 SPU + 7 SKU，覆盖全部 5 类原材料。

## 7. 测试

- 后端：`MaterialCategoryControllerTest`(7) / `SpuControllerTest`(7) / `SkuControllerTest`(8，含 jsonb spec 回读、同尺寸多规格、spuId 校验)。全后端 **75** 用例全绿。
- 前端：`api/{materialCategory,spu,sku}.test.ts` 各 6 端点，共 18 用例。
- 契约：`validate.mjs` 断言三资源关键端点 + `ItemType` 枚举（RAW/SEMI/FINISHED）齐全。

## 8. 演进记录

- v1（Phase A）：物料目录 `material_category`/`spu`/`sku`（V5）。`item_type` 置 SKU 级、`spec` 用 jsonb。
- 后续：Phase B 包装/条码、Phase C 库存/托盘（需求 2 的库存与托盘统计在此落地）。
