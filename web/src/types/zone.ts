/* ========================================
   库区模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 库区详情（对齐 Zone schema） */
export interface Zone {
  id: number;
  warehouseId: number;
  warehouseName?: string | null;
  code: string;
  name: string;
  type?: string | null;
  remark?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 库区列表项（对齐 ZoneSummary schema） */
export interface ZoneSummary {
  id: number;
  warehouseId: number;
  warehouseName?: string | null;
  code: string;
  name: string;
  type?: string | null;
  status: 0 | 1;
  createdAt: string;
}

/** 库区列表分页响应 */
export interface ZoneListResponse {
  items: ZoneSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建库区请求 */
export interface CreateZoneRequest {
  warehouseId: number;
  code: string;
  name: string;
  type?: string;
  remark?: string;
  status?: 0 | 1;
}

/** 更新库区请求（warehouseId/code 不可改，状态走独立接口） */
export interface UpdateZoneRequest {
  name?: string;
  type?: string;
  remark?: string;
}

/** 库区查询参数 */
export interface ZoneQueryParams {
  keyword?: string;
  warehouseId?: number;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
