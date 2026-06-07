/* ========================================
   供应商模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 供应商详情（对齐 Supplier schema） */
export interface Supplier {
  id: number;
  code: string;
  name: string;
  address: string;
  contact?: string | null;
  phone?: string | null;
  email?: string | null;
  remark?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 供应商列表项（对齐 SupplierSummary schema） */
export interface SupplierSummary {
  id: number;
  code: string;
  name: string;
  address: string;
  contact?: string | null;
  phone?: string | null;
  status: 0 | 1;
  createdAt: string;
}

/** 供应商列表分页响应 */
export interface SupplierListResponse {
  items: SupplierSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建供应商请求 */
export interface CreateSupplierRequest {
  code: string;
  name: string;
  address: string;
  contact?: string;
  phone?: string;
  email?: string;
  remark?: string;
  status?: 0 | 1;
}

/** 更新供应商请求（code 不可改，状态走独立接口） */
export interface UpdateSupplierRequest {
  name?: string;
  address?: string;
  contact?: string;
  phone?: string;
  email?: string;
  remark?: string;
}

/** 供应商查询参数 */
export interface SupplierQueryParams {
  keyword?: string;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
