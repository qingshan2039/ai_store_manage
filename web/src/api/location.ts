/* 库位模块 API（严格对齐 OpenAPI 契约） */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  Location,
  LocationListResponse,
  CreateLocationRequest,
  UpdateLocationRequest,
  LocationQueryParams,
} from '@/types/location';

export const locationApi = {
  create(data: CreateLocationRequest) {
    return request.post<Location>('/api/locations', data);
  },
  list(params: LocationQueryParams) {
    return request.get<LocationListResponse>('/api/locations', { params });
  },
  getById(id: number) {
    return request.get<Location>(`/api/locations/${id}`);
  },
  update(id: number, data: UpdateLocationRequest) {
    return request.put<Location>(`/api/locations/${id}`, data);
  },
  delete(id: number) {
    return request.delete(`/api/locations/${id}`);
  },
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<Location>(`/api/locations/${id}/status`, data);
  },
};
