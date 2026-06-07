/* ========================================
   物料品类模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  MaterialCategory,
  MaterialCategoryListResponse,
  CreateMaterialCategoryRequest,
  UpdateMaterialCategoryRequest,
  MaterialCategoryQueryParams,
} from '@/types/materialCategory';

export const materialCategoryApi = {
  /** 创建品类 — POST /api/material-categories → 201 */
  create(data: CreateMaterialCategoryRequest) {
    return request.post<MaterialCategory>('/api/material-categories', data);
  },

  /** 查询品类列表 — GET /api/material-categories → 200 */
  list(params: MaterialCategoryQueryParams) {
    return request.get<MaterialCategoryListResponse>('/api/material-categories', { params });
  },

  /** 查询品类详情 — GET /api/material-categories/:id → 200 */
  getById(id: number) {
    return request.get<MaterialCategory>(`/api/material-categories/${id}`);
  },

  /** 更新品类 — PUT /api/material-categories/:id → 200 */
  update(id: number, data: UpdateMaterialCategoryRequest) {
    return request.put<MaterialCategory>(`/api/material-categories/${id}`, data);
  },

  /** 删除品类 — DELETE /api/material-categories/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/material-categories/${id}`);
  },

  /** 变更品类状态 — PATCH /api/material-categories/:id/status → 200 */
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<MaterialCategory>(`/api/material-categories/${id}/status`, data);
  },
};
