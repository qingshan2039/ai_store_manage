/* ========================================
   顾客模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type {
  Customer,
  CustomerListResponse,
  CreateCustomerRequest,
  UpdateCustomerRequest,
  UpdateCustomerStatusRequest,
  CustomerQueryParams,
} from '@/types/customer';

export const customerApi = {
  /** 创建顾客 — POST /api/customers → 201 */
  create(data: CreateCustomerRequest) {
    return request.post<Customer>('/api/customers', data);
  },

  /** 查询顾客列表 — GET /api/customers → 200 */
  list(params: CustomerQueryParams) {
    return request.get<CustomerListResponse>('/api/customers', { params });
  },

  /** 查询顾客详情 — GET /api/customers/:id → 200 */
  getById(id: number) {
    return request.get<Customer>(`/api/customers/${id}`);
  },

  /** 更新顾客 — PUT /api/customers/:id → 200 */
  update(id: number, data: UpdateCustomerRequest) {
    return request.put<Customer>(`/api/customers/${id}`, data);
  },

  /** 删除顾客 — DELETE /api/customers/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/customers/${id}`);
  },

  /** 变更顾客状态 — PATCH /api/customers/:id/status → 200 */
  updateStatus(id: number, data: UpdateCustomerStatusRequest) {
    return request.patch<Customer>(`/api/customers/${id}/status`, data);
  },
};
