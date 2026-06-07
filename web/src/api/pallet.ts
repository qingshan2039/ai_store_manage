/* ========================================
   托盘类型模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  PalletType,
  PalletTypeListResponse,
  CreatePalletTypeRequest,
  UpdatePalletTypeRequest,
  PalletTypeQueryParams,
} from '@/types/pallet';

export const palletTypeApi = {
  /** 创建托盘类型 — POST /api/pallet-types → 201 */
  create(data: CreatePalletTypeRequest) {
    return request.post<PalletType>('/api/pallet-types', data);
  },

  /** 查询托盘类型列表 — GET /api/pallet-types → 200 */
  list(params: PalletTypeQueryParams) {
    return request.get<PalletTypeListResponse>('/api/pallet-types', { params });
  },

  /** 查询托盘类型详情 — GET /api/pallet-types/:id → 200 */
  getById(id: number) {
    return request.get<PalletType>(`/api/pallet-types/${id}`);
  },

  /** 更新托盘类型 — PUT /api/pallet-types/:id → 200 */
  update(id: number, data: UpdatePalletTypeRequest) {
    return request.put<PalletType>(`/api/pallet-types/${id}`, data);
  },

  /** 删除托盘类型 — DELETE /api/pallet-types/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/pallet-types/${id}`);
  },

  /** 变更托盘类型状态 — PATCH /api/pallet-types/:id/status → 200 */
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<PalletType>(`/api/pallet-types/${id}/status`, data);
  },
};
