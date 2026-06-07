/* ========================================
   仓库模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 仓库类型（对齐契约 WarehouseType） */
export type WarehouseType = 'RAW' | 'SEMI' | 'FINISHED';

/** 仓库详情（对齐 Warehouse schema） */
export interface Warehouse {
  id: number;
  code: string;
  name: string;
  type: WarehouseType;
  remark?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 仓库列表项（对齐 WarehouseSummary schema） */
export interface WarehouseSummary {
  id: number;
  code: string;
  name: string;
  type: WarehouseType;
  status: 0 | 1;
  createdAt: string;
}

/** 仓库列表分页响应 */
export interface WarehouseListResponse {
  items: WarehouseSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建仓库请求 */
export interface CreateWarehouseRequest {
  code: string;
  name: string;
  type: WarehouseType;
  remark?: string;
  status?: 0 | 1;
}

/** 更新仓库请求（code 不可改，状态走独立接口） */
export interface UpdateWarehouseRequest {
  name?: string;
  type?: WarehouseType;
  remark?: string;
}

/** 仓库查询参数 */
export interface WarehouseQueryParams {
  keyword?: string;
  type?: WarehouseType;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
