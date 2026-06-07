# 库存与托盘模块（Phase C：库位 / 托盘 LPN / 库存 + 统计）功能设计与实现说明

> 总体设计见 [../design/overview.md](../design/overview.md)；物料目录见 [material-catalog.md](material-catalog.md)、包装条码见 [packaging-barcode.md](packaging-barcode.md)；运行测试见 [../design/testing.md](../design/testing.md)。

## 1. 概述

原材料管理 **Phase C**（收尾）：在目录（A）+ 包装（B）之上建立**库位、托盘实例、库存**，并兑现**需求②「既统计库存数量，又统计托盘数量」与整托/尾托**。

## 2. 需求② 的实现（核心）

> 因存在不满托（480 而非 500），**托盘数量不能用「总库存 ÷ 每托数」算**——会把尾托算错。故引入 `lpn`（托盘实例），库存行挂到托盘上：

- **库存数量** = `SUM(inventory.qty_on_hand)`（基本单位，永远准确）。
- **托盘数量** = `COUNT(DISTINCT inventory.lpn_id)`（数真实托盘，不满托也算一托）。
- **整托/尾托** = 每托 `SUM(qty_on_hand)` 与「标准每托数」比较：等于/超出为整托，小于为尾托。标准每托数由 [B 的 `packaging_relation`](packaging-barcode.md) 沿 `is_fixed_qty=1` 关系逐级乘 `child_qty` 推导。

端点 `GET /api/inventory/summary?skuId=&warehouseId=` 返回 `{ totalQty, palletCount, recordCount, standardPalletQty, pallets:[{lpnCode, qty, fullPallet}] }`。

实测：某 SKU 两托（500 + 480）→ **总库存 980、托盘数 2、标准每托 500、500=整托 / 480=尾托**。

## 3. 数据模型（PostgreSQL，迁移 [`V7__add_inventory_lpn.sql`](../../api/src/main/resources/db/migration/V7__add_inventory_lpn.sql)）

| 表 | 关键字段 | 说明 |
| --- | --- | --- |
| `location` | warehouse_id / **zone_id（可空）** / code / loc_type | 库位，**接已建 zone**：仓库→库区→库位；`(warehouse_id, code)` 唯一 |
| `lpn` | lpn_code(SSCC) / pallet_type_id / warehouse_id / location_id(可空) / **status** / gross_weight | 托盘实例；状态 `LpnStatus` 在库/在途/空置；`lpn_code` 唯一 |
| `inventory` | sku_id / **lpn_id(可空)** / location_id(可空) / lot_no / mfg_date / exp_date / qty_on_hand / qty_reserved | 库存，基本单位记账；`qtyAvailable=on_hand-reserved`（计算） |

## 4. 接口契约（3 模块 × 6 端点 + 统计）

契约源 [`api-contract/openapi.yaml`](../../api-contract/openapi.yaml)。路径 `/api/locations`、`/api/lpns`、`/api/inventory`。LPN 状态变更走 `PATCH /api/lpns/{id}/status`（`UpdateLpnStatusRequest`，非 0/1）。库存额外 `GET /api/inventory/summary`。

`LpnStatus` 枚举 `IN_STOCK/IN_TRANSIT/EMPTY`。错误码：`DUPLICATE_LOCATION_CODE` / `DUPLICATE_LPN_CODE`(409)、`LOCATION_NOT_FOUND` / `LPN_NOT_FOUND` / `INVENTORY_NOT_FOUND`(404)、引用父级 `WAREHOUSE/ZONE/PALLET_TYPE/SKU_NOT_FOUND`、`VALIDATION_ERROR`(400)。

## 5. 关键设计

- **LPN 实体化是需求② 的关键**：不靠除法、按真实托盘计数，整托/尾托各算各的。
- **LOCATION 接已建 ZONE**：`location.zone_id` 外键到 zone（可空，允许仅挂仓库）。
- **库存统计跨 B/C**：标准每托数从 B 的 `packaging_relation` 推导；故 B 必须先于 C。
- **关联名/编码回填**：库位回填仓库/库区名；托盘回填类型/仓库/库位；库存回填 SKU/托盘/库位。
- 前端「库存管理」菜单：库位管理 / 托盘实例 / 库存查询 / **库存统计**（按 SKU 看总库存+托盘数+整托尾托）。

## 6. 实现与验证

- 后端 3 模块（location 13 / lpn 15 / inventory 16 文件）+ V7 迁移 + `LpnStatus` 枚举 + 统计逻辑；编译通过，V7 迁移到 v7。
- E2E（真实 PostgreSQL）：库位/托盘/库存创建、托盘状态机、**统计 500整托+480尾托/托盘数2/标准每托500** 全通过。
- 前端 3 模块 + 库存统计页；浏览器实测统计页 SKU 选择后展示 980/2/500 与整托·尾托 Tag。`tsc -b && vite build` 通过。

## 7. 测试

- 后端：`LocationControllerTest`(6) / `LpnControllerTest`(6，含状态机) / `InventoryControllerTest`(7，**含 summary 全链：980/2/500/整托尾托**)。
- 前端：`api/{location,lpn,inventory}.test.ts`，共 18 用例（含 summary 端点）。
- 契约：`validate.mjs` 断言 3 资源关键端点 + `/inventory/summary` + `LpnStatus` 枚举齐全。

## 8. 全量收官

物料主数据 12 张表全部落地：A 目录（品类/SPU/SKU）+ B 包装条码（5 表）+ C 库存托盘（3 表），复用既有 warehouse/pallet_type。三个原始需求①②③ 全部兑现。后续「入库/出库/调拨/盘点」业务操作为预留扩展。
