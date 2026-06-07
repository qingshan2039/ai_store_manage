/* 计量换算模块 API（严格对齐 OpenAPI 契约） */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  UnitConversion,
  UnitConversionListResponse,
  CreateUnitConversionRequest,
  UpdateUnitConversionRequest,
  UnitConversionQueryParams,
} from '@/types/unitConversion';

export const unitConversionApi = {
  create(data: CreateUnitConversionRequest) {
    return request.post<UnitConversion>('/api/unit-conversions', data);
  },
  list(params: UnitConversionQueryParams) {
    return request.get<UnitConversionListResponse>('/api/unit-conversions', { params });
  },
  getById(id: number) {
    return request.get<UnitConversion>(`/api/unit-conversions/${id}`);
  },
  update(id: number, data: UpdateUnitConversionRequest) {
    return request.put<UnitConversion>(`/api/unit-conversions/${id}`, data);
  },
  delete(id: number) {
    return request.delete(`/api/unit-conversions/${id}`);
  },
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<UnitConversion>(`/api/unit-conversions/${id}/status`, data);
  },
};
