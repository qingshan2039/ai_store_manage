/* ========================================
   顾客模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 送货地址（响应） */
export interface ShipAddress {
  id?: number;
  address: string;
  remark?: string | null;
}

/** 送货地址输入（创建/更新） */
export interface ShipAddressInput {
  address: string;
  remark?: string;
}

/** 顾客详情（对齐 Customer schema） */
export interface Customer {
  id: number;
  code: string;
  name: string;
  address: string;
  /** 收/发货地址列表（连锁客户可有多个） */
  shipAddresses: ShipAddress[];
  contact?: string | null;
  phone?: string | null;
  email?: string | null;
  remark?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 顾客列表项（对齐 CustomerSummary schema） */
export interface CustomerSummary {
  id: number;
  code: string;
  name: string;
  address: string;
  shipAddresses: ShipAddress[];
  contact?: string | null;
  phone?: string | null;
  status: 0 | 1;
  createdAt: string;
}

/** 顾客列表分页响应 */
export interface CustomerListResponse {
  items: CustomerSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建顾客请求 */
export interface CreateCustomerRequest {
  code: string;
  name: string;
  address: string;
  shipAddresses: ShipAddressInput[];
  contact?: string;
  phone?: string;
  email?: string;
  remark?: string;
  status?: 0 | 1;
}

/** 更新顾客请求（code 不可改，状态走独立接口；shipAddresses 提供则整列表替换） */
export interface UpdateCustomerRequest {
  name?: string;
  address?: string;
  shipAddresses?: ShipAddressInput[];
  contact?: string;
  phone?: string;
  email?: string;
  remark?: string;
}

/** 状态变更请求 */
export interface UpdateCustomerStatusRequest {
  status: 0 | 1;
}

/** 顾客查询参数 */
export interface CustomerQueryParams {
  keyword?: string;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
