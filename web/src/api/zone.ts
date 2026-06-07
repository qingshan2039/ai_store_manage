/* ========================================
   库区模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  Zone,
  ZoneListResponse,
  CreateZoneRequest,
  UpdateZoneRequest,
  ZoneQueryParams,
} from '@/types/zone';

export const zoneApi = {
  /** 创建库区 — POST /api/zones → 201 */
  create(data: CreateZoneRequest) {
    return request.post<Zone>('/api/zones', data);
  },

  /** 查询库区列表 — GET /api/zones → 200 */
  list(params: ZoneQueryParams) {
    return request.get<ZoneListResponse>('/api/zones', { params });
  },

  /** 查询库区详情 — GET /api/zones/:id → 200 */
  getById(id: number) {
    return request.get<Zone>(`/api/zones/${id}`);
  },

  /** 更新库区 — PUT /api/zones/:id → 200 */
  update(id: number, data: UpdateZoneRequest) {
    return request.put<Zone>(`/api/zones/${id}`, data);
  },

  /** 删除库区 — DELETE /api/zones/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/zones/${id}`);
  },

  /** 变更库区状态 — PATCH /api/zones/:id/status → 200 */
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<Zone>(`/api/zones/${id}/status`, data);
  },
};
