/* 包装关系模块 API（严格对齐 OpenAPI 契约） */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  PackagingRelation,
  PackagingRelationListResponse,
  CreatePackagingRelationRequest,
  UpdatePackagingRelationRequest,
  PackagingRelationQueryParams,
} from '@/types/packagingRelation';

export const packagingRelationApi = {
  create(data: CreatePackagingRelationRequest) {
    return request.post<PackagingRelation>('/api/packaging-relations', data);
  },
  list(params: PackagingRelationQueryParams) {
    return request.get<PackagingRelationListResponse>('/api/packaging-relations', { params });
  },
  getById(id: number) {
    return request.get<PackagingRelation>(`/api/packaging-relations/${id}`);
  },
  update(id: number, data: UpdatePackagingRelationRequest) {
    return request.put<PackagingRelation>(`/api/packaging-relations/${id}`, data);
  },
  delete(id: number) {
    return request.delete(`/api/packaging-relations/${id}`);
  },
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<PackagingRelation>(`/api/packaging-relations/${id}/status`, data);
  },
};
