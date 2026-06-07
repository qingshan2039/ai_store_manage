/* ========================================
   SPU 模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** SPU 详情（对齐 Spu schema） */
export interface Spu {
  id: number;
  spuCode: string;
  spuName: string;
  categoryCode: string;
  categoryName?: string | null;
  brand?: string | null;
  baseUnit: string;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** SPU 列表项（对齐 SpuSummary schema） */
export interface SpuSummary {
  id: number;
  spuCode: string;
  spuName: string;
  categoryCode: string;
  categoryName?: string | null;
  baseUnit: string;
  status: 0 | 1;
  createdAt: string;
}

/** SPU 列表分页响应 */
export interface SpuListResponse {
  items: SpuSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建 SPU 请求 */
export interface CreateSpuRequest {
  spuCode: string;
  spuName: string;
  categoryCode: string;
  brand?: string;
  baseUnit: string;
  status?: 0 | 1;
}

/** 更新 SPU 请求（spu_code 不可改，状态走独立接口） */
export interface UpdateSpuRequest {
  spuName?: string;
  categoryCode?: string;
  brand?: string;
  baseUnit?: string;
}

/** SPU 查询参数 */
export interface SpuQueryParams {
  keyword?: string;
  categoryCode?: string;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
