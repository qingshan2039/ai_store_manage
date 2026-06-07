/* ========================================
   车辆模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  Vehicle,
  VehicleListResponse,
  CreateVehicleRequest,
  UpdateVehicleRequest,
  VehicleQueryParams,
} from '@/types/vehicle';

export const vehicleApi = {
  /** 创建车辆 — POST /api/vehicles → 201 */
  create(data: CreateVehicleRequest) {
    return request.post<Vehicle>('/api/vehicles', data);
  },

  /** 查询车辆列表 — GET /api/vehicles → 200 */
  list(params: VehicleQueryParams) {
    return request.get<VehicleListResponse>('/api/vehicles', { params });
  },

  /** 查询车辆详情 — GET /api/vehicles/:id → 200 */
  getById(id: number) {
    return request.get<Vehicle>(`/api/vehicles/${id}`);
  },

  /** 更新车辆 — PUT /api/vehicles/:id → 200 */
  update(id: number, data: UpdateVehicleRequest) {
    return request.put<Vehicle>(`/api/vehicles/${id}`, data);
  },

  /** 删除车辆 — DELETE /api/vehicles/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/vehicles/${id}`);
  },

  /** 变更车辆状态 — PATCH /api/vehicles/:id/status → 200 */
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<Vehicle>(`/api/vehicles/${id}/status`, data);
  },
};
