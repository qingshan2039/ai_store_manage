/* ========================================
   SPU 模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  Spu,
  SpuListResponse,
  CreateSpuRequest,
  UpdateSpuRequest,
  SpuQueryParams,
} from '@/types/spu';

export const spuApi = {
  /** 创建 SPU — POST /api/spus → 201 */
  create(data: CreateSpuRequest) {
    return request.post<Spu>('/api/spus', data);
  },

  /** 查询 SPU 列表 — GET /api/spus → 200 */
  list(params: SpuQueryParams) {
    return request.get<SpuListResponse>('/api/spus', { params });
  },

  /** 查询 SPU 详情 — GET /api/spus/:id → 200 */
  getById(id: number) {
    return request.get<Spu>(`/api/spus/${id}`);
  },

  /** 更新 SPU — PUT /api/spus/:id → 200 */
  update(id: number, data: UpdateSpuRequest) {
    return request.put<Spu>(`/api/spus/${id}`, data);
  },

  /** 删除 SPU — DELETE /api/spus/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/spus/${id}`);
  },

  /** 变更 SPU 状态 — PATCH /api/spus/:id/status → 200 */
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<Spu>(`/api/spus/${id}/status`, data);
  },
};
