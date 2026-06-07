/* ========================================
   托盘类型模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 托盘类型详情（对齐 PalletType schema） */
export interface PalletType {
  id: number;
  code: string;
  name: string;
  length: number;
  width: number;
  tareWeight?: number | null;
  maxLoad?: number | null;
  maxStack?: number | null;
  remark?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 托盘类型列表项（对齐 PalletTypeSummary schema） */
export interface PalletTypeSummary {
  id: number;
  code: string;
  name: string;
  length: number;
  width: number;
  maxLoad?: number | null;
  maxStack?: number | null;
  status: 0 | 1;
  createdAt: string;
}

/** 托盘类型列表分页响应 */
export interface PalletTypeListResponse {
  items: PalletTypeSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建托盘类型请求 */
export interface CreatePalletTypeRequest {
  code: string;
  name: string;
  length: number;
  width: number;
  tareWeight?: number;
  maxLoad?: number;
  maxStack?: number;
  remark?: string;
  status?: 0 | 1;
}

/** 更新托盘类型请求（code 不可改，状态走独立接口） */
export interface UpdatePalletTypeRequest {
  name?: string;
  length?: number;
  width?: number;
  tareWeight?: number;
  maxLoad?: number;
  maxStack?: number;
  remark?: string;
}

/** 托盘类型查询参数 */
export interface PalletTypeQueryParams {
  keyword?: string;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
