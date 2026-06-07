/* ========================================
   打油记录模块 API（严格对齐 OpenAPI 契约，流水无状态切换）
   ======================================== */
import request from './request';
import type {
  FuelRecord,
  FuelRecordListResponse,
  CreateFuelRecordRequest,
  UpdateFuelRecordRequest,
  FuelRecordQueryParams,
} from '@/types/fuelRecord';

export const fuelRecordApi = {
  /** 创建打油记录 — POST /api/fuel-records → 201 */
  create(data: CreateFuelRecordRequest) {
    return request.post<FuelRecord>('/api/fuel-records', data);
  },

  /** 查询打油记录列表 — GET /api/fuel-records → 200 */
  list(params: FuelRecordQueryParams) {
    return request.get<FuelRecordListResponse>('/api/fuel-records', { params });
  },

  /** 查询打油记录详情 — GET /api/fuel-records/:id → 200 */
  getById(id: number) {
    return request.get<FuelRecord>(`/api/fuel-records/${id}`);
  },

  /** 更新打油记录 — PUT /api/fuel-records/:id → 200 */
  update(id: number, data: UpdateFuelRecordRequest) {
    return request.put<FuelRecord>(`/api/fuel-records/${id}`, data);
  },

  /** 删除打油记录 — DELETE /api/fuel-records/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/fuel-records/${id}`);
  },
};
