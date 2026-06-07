/* ========================================
   SKU 模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  Sku,
  SkuListResponse,
  CreateSkuRequest,
  UpdateSkuRequest,
  SkuQueryParams,
} from '@/types/sku';

export const skuApi = {
  /** 创建 SKU — POST /api/skus → 201 */
  create(data: CreateSkuRequest) {
    return request.post<Sku>('/api/skus', data);
  },

  /** 查询 SKU 列表 — GET /api/skus → 200 */
  list(params: SkuQueryParams) {
    return request.get<SkuListResponse>('/api/skus', { params });
  },

  /** 查询 SKU 详情 — GET /api/skus/:id → 200 */
  getById(id: number) {
    return request.get<Sku>(`/api/skus/${id}`);
  },

  /** 更新 SKU — PUT /api/skus/:id → 200 */
  update(id: number, data: UpdateSkuRequest) {
    return request.put<Sku>(`/api/skus/${id}`, data);
  },

  /** 删除 SKU — DELETE /api/skus/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/skus/${id}`);
  },

  /** 变更 SKU 状态 — PATCH /api/skus/:id/status → 200 */
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<Sku>(`/api/skus/${id}/status`, data);
  },
};
