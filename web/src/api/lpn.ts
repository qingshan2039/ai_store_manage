/* 托盘实例模块 API（严格对齐 OpenAPI 契约） */
import request from './request';
import type {
  Lpn,
  LpnListResponse,
  CreateLpnRequest,
  UpdateLpnRequest,
  UpdateLpnStatusRequest,
  LpnQueryParams,
} from '@/types/lpn';

export const lpnApi = {
  create(data: CreateLpnRequest) {
    return request.post<Lpn>('/api/lpns', data);
  },
  list(params: LpnQueryParams) {
    return request.get<LpnListResponse>('/api/lpns', { params });
  },
  getById(id: number) {
    return request.get<Lpn>(`/api/lpns/${id}`);
  },
  update(id: number, data: UpdateLpnRequest) {
    return request.put<Lpn>(`/api/lpns/${id}`, data);
  },
  delete(id: number) {
    return request.delete(`/api/lpns/${id}`);
  },
  /** 变更托盘状态（在库/在途/空置） */
  updateStatus(id: number, data: UpdateLpnStatusRequest) {
    return request.patch<Lpn>(`/api/lpns/${id}/status`, data);
  },
};
