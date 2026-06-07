/* 物料图片模块 API（严格对齐 OpenAPI 契约） */
import request from './request';
import type { UpdateStatusRequest } from '@/types/common';
import type {
  ItemImage,
  ItemImageListResponse,
  CreateItemImageRequest,
  UpdateItemImageRequest,
  ItemImageQueryParams,
} from '@/types/itemImage';

export const itemImageApi = {
  create(data: CreateItemImageRequest) {
    return request.post<ItemImage>('/api/item-images', data);
  },
  list(params: ItemImageQueryParams) {
    return request.get<ItemImageListResponse>('/api/item-images', { params });
  },
  getById(id: number) {
    return request.get<ItemImage>(`/api/item-images/${id}`);
  },
  update(id: number, data: UpdateItemImageRequest) {
    return request.put<ItemImage>(`/api/item-images/${id}`, data);
  },
  delete(id: number) {
    return request.delete(`/api/item-images/${id}`);
  },
  updateStatus(id: number, data: UpdateStatusRequest) {
    return request.patch<ItemImage>(`/api/item-images/${id}/status`, data);
  },
};
