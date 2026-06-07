/* ========================================
   仓库模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  Warehouse,
  WarehouseListResponse,
  CreateWarehouseRequest,
  UpdateWarehouseRequest,
  WarehouseQueryParams,
} from '@/types/warehouse';

export const warehouseApi = {
  /** 创建仓库 — POST /api/warehouses → 201 */
  create(data: CreateWarehouseRequest) {
    return request.post<Warehouse>('/api/warehouses', data);
  },

  /** 查询仓库列表 — GET /api/warehouses → 200 */
  list(params: WarehouseQueryParams) {
    return request.get<WarehouseListResponse>('/api/warehouses', { params });
  },

  /** 查询仓库详情 — GET /api/warehouses/:id → 200 */
  getById(id: number) {
    return request.get<Warehouse>(`/api/warehouses/${id}`);
  },

  /** 更新仓库 — PUT /api/warehouses/:id → 200 */
  update(id: number, data: UpdateWarehouseRequest) {
    return request.put<Warehouse>(`/api/warehouses/${id}`, data);
  },

  /** 删除仓库 — DELETE /api/warehouses/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/warehouses/${id}`);
  },

  /** 变更仓库状态 — PATCH /api/warehouses/:id/status → 200 */
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<Warehouse>(`/api/warehouses/${id}/status`, data);
  },
};
