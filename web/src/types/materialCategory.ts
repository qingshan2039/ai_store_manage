/* ========================================
   物料品类模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 物料品类详情（对齐 MaterialCategory schema） */
export interface MaterialCategory {
  id: number;
  code: string;
  name: string;
  sortOrder: number;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 物料品类列表项（对齐 MaterialCategorySummary schema） */
export interface MaterialCategorySummary {
  id: number;
  code: string;
  name: string;
  sortOrder: number;
  status: 0 | 1;
  createdAt: string;
}

/** 物料品类列表分页响应 */
export interface MaterialCategoryListResponse {
  items: MaterialCategorySummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建物料品类请求 */
export interface CreateMaterialCategoryRequest {
  code: string;
  name: string;
  sortOrder?: number;
  status?: 0 | 1;
}

/** 更新物料品类请求（code 不可改，状态走独立接口） */
export interface UpdateMaterialCategoryRequest {
  name?: string;
  sortOrder?: number;
}

/** 物料品类查询参数 */
export interface MaterialCategoryQueryParams {
  keyword?: string;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
