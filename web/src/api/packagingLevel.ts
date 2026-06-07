/* 包装层级模块 API（严格对齐 OpenAPI 契约） */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  PackagingLevel,
  PackagingLevelListResponse,
  CreatePackagingLevelRequest,
  UpdatePackagingLevelRequest,
  PackagingLevelQueryParams,
} from '@/types/packagingLevel';

export const packagingLevelApi = {
  create(data: CreatePackagingLevelRequest) {
    return request.post<PackagingLevel>('/api/packaging-levels', data);
  },
  list(params: PackagingLevelQueryParams) {
    return request.get<PackagingLevelListResponse>('/api/packaging-levels', { params });
  },
  getById(id: number) {
    return request.get<PackagingLevel>(`/api/packaging-levels/${id}`);
  },
  update(id: number, data: UpdatePackagingLevelRequest) {
    return request.put<PackagingLevel>(`/api/packaging-levels/${id}`, data);
  },
  delete(id: number) {
    return request.delete(`/api/packaging-levels/${id}`);
  },
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<PackagingLevel>(`/api/packaging-levels/${id}/status`, data);
  },
};
