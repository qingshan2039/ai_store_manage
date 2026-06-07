/* 库存模块 API（严格对齐 OpenAPI 契约） */
import request from './request';
import type {
  Inventory,
  InventoryListResponse,
  CreateInventoryRequest,
  UpdateInventoryRequest,
  InventoryQueryParams,
  InventorySummary,
} from '@/types/inventory';

export const inventoryApi = {
  create(data: CreateInventoryRequest) {
    return request.post<Inventory>('/api/inventory', data);
  },
  list(params: InventoryQueryParams) {
    return request.get<InventoryListResponse>('/api/inventory', { params });
  },
  /** 库存统计：库存数量 + 托盘数量 + 整托/尾托（需求②） */
  summary(params: { skuId: number; warehouseId?: number }) {
    return request.get<InventorySummary>('/api/inventory/summary', { params });
  },
  getById(id: number) {
    return request.get<Inventory>(`/api/inventory/${id}`);
  },
  update(id: number, data: UpdateInventoryRequest) {
    return request.put<Inventory>(`/api/inventory/${id}`, data);
  },
  delete(id: number) {
    return request.delete(`/api/inventory/${id}`);
  },
};
