# 包装与条码模块（Phase B：层级 / 关系 / 条码 / 换算 / 图片）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；物料目录见 [material-catalog.md](material-catalog.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

原材料管理 **Phase B**：在物料目录（SKU）之上建立**多级包装、父子关系、条码、单位换算、图片**。其中 `packaging_relation` 的 `child_qty` + `is_fixed_qty` 是需求②「每托 500/480、12/16/18」与「整托/尾托」判定的数据基础（统计在 **Phase C** 兑现）。

## 2. 数据模型（PostgreSQL，迁移 [`V6__add_packaging_barcode.sql`](../../api/src/main/resources/db/migration/V6__add_packaging_barcode.sql)）

| 表 | 关键字段 | 说明 |
| --- | --- | --- |
| `packaging_level` | sku_id / level_name(卷/箱/托) / level_seq / unit_code / 尺寸 / 净毛重 / is_base_unit / is_sellable | SKU 的多级包装；`(sku_id, level_seq)` 唯一 |
| `packaging_relation` | parent_level_id / child_level_id / **child_qty** / **is_fixed_qty** / tare_weight | 1 父层含子层数量（如 1 托=500 箱）；`is_fixed_qty=1` 整托、`0` 尾托；`(parent, child)` 唯一 |
| `barcode` | level_id / barcode / barcode_type(EAN13/ITF14/SSCC/OTHER) / is_primary / valid_from/to | 包装层条码；`barcode` 全局唯一 |
| `unit_conversion` | sku_id / from_unit / to_unit / factor | SKU 单位换算；`(sku_id, from_unit, to_unit)` 唯一 |
| `item_image` | spu_id / sku_id / level_id（均可空）/ image_url / image_type / sort_order / is_primary | 物料图片，**仅存 URL**（不做文件上传） |

布尔类字段（is_base_unit/is_sellable/is_fixed_qty/is_primary）以 SMALLINT 0/1 存储，对齐 status 约定。

## 3. 接口契约（5 模块 × 6 端点）

契约源 [`api-contract/openapi.yaml`](../../api-contract/openapi.yaml)。路径：`/api/packaging-levels`、`/api/packaging-relations`、`/api/barcodes`、`/api/unit-conversions`、`/api/item-images`，各 6 端点（CRUD + 状态）。`BarcodeType` 枚举 `EAN13/ITF14/SSCC/OTHER`。

错误码：`DUPLICATE_PACKAGING_LEVEL_SEQ` / `DUPLICATE_PACKAGING_RELATION` / `DUPLICATE_BARCODE` / `DUPLICATE_UNIT_CONVERSION`(409)、各 `*_NOT_FOUND`(404)、引用父级不存在（`SKU_NOT_FOUND` / `PACKAGING_LEVEL_NOT_FOUND`）、`VALIDATION_ERROR`(400)。

## 4. 关键设计

- **关联名回填**：层级回填 `skuName`；关系回填父/子 `levelName`；条码回填 `levelName`；换算回填 `skuName`（批量 `selectBatchIds` / `in`）。
- **引用完整性应用层校验**：层级校验 `sku_id`、关系校验父子层、条码校验 `level_id` 存在。
- **包装层级是「料的包装」**，与 SKU 上的「料本身尺寸」（material-catalog）正交，各管各的。
- 前端单列「包装与条码」菜单组，5 个列表页；包装关系列表直观展示 `父层 → 子层 / child_qty / 整托·尾托 Tag`。

## 5. 实现与验证

- 后端 5 模块各 13 文件 + V6 迁移 + `BarcodeType` 枚举；编译通过，V6 自动迁移到 v6。
- E2E（真实 PostgreSQL）：建 卷/箱/托 三层 + 托→箱 `child_qty=500 is_fixed_qty=1`、条码（EAN13，重复→409）、换算（ROLL→M2 ×300）、图片均通过。
- 前端 5 模块（types/api/列表/表单/搜索）+ 「包装与条码」菜单/路由/面包屑；浏览器实测包装关系页展示「托 → 箱 / 500 / 定量整托」。`tsc -b && vite build` 通过。

## 6. 测试

- 后端：5 个 `*ControllerTest`，共 26 用例（含层级序号/关系/条码/换算唯一冲突、父级 404、jsonb 无关的关联名校验）。
- 前端：`api/{packagingLevel,packagingRelation,barcode,unitConversion,itemImage}.test.ts`，共 30 用例。
- 契约：`validate.mjs` 断言 5 资源关键端点 + `BarcodeType` 枚举齐全。

## 7. 演进

- v1（Phase B）：包装层级/关系、条码、换算、图片（V6）。
- 下一步 Phase C：`location`/`lpn`/`inventory` —— 用本阶段 `packaging_relation.child_qty` 判整托/尾托，兑现需求②的库存数量与托盘数量统计。
