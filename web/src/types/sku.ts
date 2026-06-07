/* ========================================
   SKU 模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 物料阶段类型 */
export type ItemType = 'RAW' | 'SEMI' | 'FINISHED';

/** SKU 详情（对齐 Sku schema） */
export interface Sku {
  id: number;
  spuId: number;
  spuCode?: string | null;
  spuName?: string | null;
  skuCode: string;
  skuName: string;
  itemType: ItemType;
  lengthMm?: number | null;
  widthMm?: number | null;
  thicknessMm?: number | null;
  rollLengthM?: number | null;
  color?: string | null;
  gsm?: number | null;
  spec?: Record<string, unknown> | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** SKU 列表项（对齐 SkuSummary schema） */
export interface SkuSummary {
  id: number;
  spuId: number;
  spuName?: string | null;
  skuCode: string;
  skuName: string;
  itemType: ItemType;
  lengthMm?: number | null;
  widthMm?: number | null;
  status: 0 | 1;
  createdAt: string;
}

/** SKU 列表分页响应 */
export interface SkuListResponse {
  items: SkuSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建 SKU 请求 */
export interface CreateSkuRequest {
  spuId: number;
  skuCode: string;
  skuName: string;
  itemType: ItemType;
  lengthMm?: number;
  widthMm?: number;
  thicknessMm?: number;
  rollLengthM?: number;
  color?: string;
  gsm?: number;
  spec?: Record<string, unknown>;
  status?: 0 | 1;
}

/** 更新 SKU 请求（spu_id/sku_code 不可改，状态走独立接口） */
export interface UpdateSkuRequest {
  skuName?: string;
  itemType?: ItemType;
  lengthMm?: number;
  widthMm?: number;
  thicknessMm?: number;
  rollLengthM?: number;
  color?: string;
  gsm?: number;
  spec?: Record<string, unknown>;
}

/** SKU 查询参数 */
export interface SkuQueryParams {
  keyword?: string;
  spuId?: number;
  itemType?: ItemType;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
