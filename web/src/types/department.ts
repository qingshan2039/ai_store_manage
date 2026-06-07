/* ========================================
   部门模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 部门类型（对齐 DepartmentType schema） */
export type DepartmentType =
  | 'WAREHOUSE'
  | 'TRANSPORT'
  | 'SALES'
  | 'PRODUCTION'
  | 'OFFICE'
  | 'HR'
  | 'FINANCE'
  | 'MANAGEMENT';

/** 部门详情（对齐 Department schema） */
export interface Department {
  id: number;
  name: string;
  code: string;
  type: DepartmentType;
  status: 0 | 1;
  sort: number;
  remark?: string | null;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 部门列表项（对齐 DepartmentSummary schema） */
export interface DepartmentSummary {
  id: number;
  name: string;
  code: string;
  type: DepartmentType;
  status: 0 | 1;
  sort: number;
  createdAt: string;
}

/** 部门列表分页响应 */
export interface DepartmentListResponse {
  items: DepartmentSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建部门请求 */
export interface CreateDepartmentRequest {
  name: string;
  code: string;
  type: DepartmentType;
  sort?: number;
  remark?: string;
  status?: 0 | 1;
}

/** 更新部门请求（code 不可改，状态走独立接口） */
export interface UpdateDepartmentRequest {
  name?: string;
  type?: DepartmentType;
  sort?: number;
  remark?: string;
}

/** 状态变更请求 */
export interface UpdateDepartmentStatusRequest {
  status: 0 | 1;
}

/** 部门查询参数 */
export interface DepartmentQueryParams {
  keyword?: string;
  type?: DepartmentType;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
