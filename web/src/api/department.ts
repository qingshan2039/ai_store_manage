/* ========================================
   部门模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type {
  Department,
  DepartmentListResponse,
  CreateDepartmentRequest,
  UpdateDepartmentRequest,
  UpdateDepartmentStatusRequest,
  DepartmentQueryParams,
} from '@/types/department';

export const departmentApi = {
  /** 创建部门 — POST /api/departments → 201 */
  create(data: CreateDepartmentRequest) {
    return request.post<Department>('/api/departments', data);
  },

  /** 查询部门列表 — GET /api/departments → 200 */
  list(params: DepartmentQueryParams) {
    return request.get<DepartmentListResponse>('/api/departments', { params });
  },

  /** 查询部门详情 — GET /api/departments/:id → 200 */
  getById(id: number) {
    return request.get<Department>(`/api/departments/${id}`);
  },

  /** 更新部门 — PUT /api/departments/:id → 200 */
  update(id: number, data: UpdateDepartmentRequest) {
    return request.put<Department>(`/api/departments/${id}`, data);
  },

  /** 删除部门 — DELETE /api/departments/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/departments/${id}`);
  },

  /** 变更部门状态 — PATCH /api/departments/:id/status → 200 */
  updateStatus(id: number, data: UpdateDepartmentStatusRequest) {
    return request.patch<Department>(`/api/departments/${id}/status`, data);
  },
};
