/* 条码模块 API（严格对齐 OpenAPI 契约） */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  Barcode,
  BarcodeListResponse,
  CreateBarcodeRequest,
  UpdateBarcodeRequest,
  BarcodeQueryParams,
} from '@/types/barcode';

export const barcodeApi = {
  create(data: CreateBarcodeRequest) {
    return request.post<Barcode>('/api/barcodes', data);
  },
  list(params: BarcodeQueryParams) {
    return request.get<BarcodeListResponse>('/api/barcodes', { params });
  },
  getById(id: number) {
    return request.get<Barcode>(`/api/barcodes/${id}`);
  },
  update(id: number, data: UpdateBarcodeRequest) {
    return request.put<Barcode>(`/api/barcodes/${id}`, data);
  },
  delete(id: number) {
    return request.delete(`/api/barcodes/${id}`);
  },
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<Barcode>(`/api/barcodes/${id}/status`, data);
  },
};
