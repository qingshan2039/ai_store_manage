/* ========================================
   供应商模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  Supplier,
  SupplierListResponse,
  CreateSupplierRequest,
  UpdateSupplierRequest,
  SupplierQueryParams,
} from '@/types/supplier';

export const supplierApi = {
  /** 创建供应商 — POST /api/suppliers → 201 */
  create(data: CreateSupplierRequest) {
    return request.post<Supplier>('/api/suppliers', data);
  },

  /** 查询供应商列表 — GET /api/suppliers → 200 */
  list(params: SupplierQueryParams) {
    return request.get<SupplierListResponse>('/api/suppliers', { params });
  },

  /** 查询供应商详情 — GET /api/suppliers/:id → 200 */
  getById(id: number) {
    return request.get<Supplier>(`/api/suppliers/${id}`);
  },

  /** 更新供应商 — PUT /api/suppliers/:id → 200 */
  update(id: number, data: UpdateSupplierRequest) {
    return request.put<Supplier>(`/api/suppliers/${id}`, data);
  },

  /** 删除供应商 — DELETE /api/suppliers/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/suppliers/${id}`);
  },

  /** 变更供应商状态 — PATCH /api/suppliers/:id/status → 200 */
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<Supplier>(`/api/suppliers/${id}/status`, data);
  },
};
