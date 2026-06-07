/* ========================================
   司机打卡模块 API（严格对齐 OpenAPI 契约，流水无状态切换）
   ======================================== */
import request from './request';
import type {
  DriverCheckin,
  DriverCheckinListResponse,
  CreateDriverCheckinRequest,
  UpdateDriverCheckinRequest,
  DriverCheckinQueryParams,
} from '@/types/driverCheckin';

export const driverCheckinApi = {
  /** 创建打卡记录 — POST /api/driver-checkins → 201 */
  create(data: CreateDriverCheckinRequest) {
    return request.post<DriverCheckin>('/api/driver-checkins', data);
  },

  /** 查询打卡记录列表 — GET /api/driver-checkins → 200 */
  list(params: DriverCheckinQueryParams) {
    return request.get<DriverCheckinListResponse>('/api/driver-checkins', { params });
  },

  /** 查询打卡记录详情 — GET /api/driver-checkins/:id → 200 */
  getById(id: number) {
    return request.get<DriverCheckin>(`/api/driver-checkins/${id}`);
  },

  /** 更新打卡记录 — PUT /api/driver-checkins/:id → 200 */
  update(id: number, data: UpdateDriverCheckinRequest) {
    return request.put<DriverCheckin>(`/api/driver-checkins/${id}`, data);
  },

  /** 删除打卡记录 — DELETE /api/driver-checkins/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/driver-checkins/${id}`);
  },
};
