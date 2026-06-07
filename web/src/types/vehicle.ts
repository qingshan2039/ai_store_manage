/* ========================================
   车辆模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 车辆详情（对齐 Vehicle schema） */
export interface Vehicle {
  id: number;
  plateNo: string;
  defaultDriverUserId?: number | null;
  defaultDriverOther?: string | null;
  defaultDriverName?: string | null;
  defaultEscortUserId?: number | null;
  defaultEscortOther?: string | null;
  defaultEscortName?: string | null;
  remark?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 车辆列表项（对齐 VehicleSummary schema） */
export interface VehicleSummary {
  id: number;
  plateNo: string;
  defaultDriverName?: string | null;
  defaultEscortName?: string | null;
  status: 0 | 1;
  createdAt: string;
}

/** 车辆列表分页响应 */
export interface VehicleListResponse {
  items: VehicleSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建车辆请求 */
export interface CreateVehicleRequest {
  plateNo: string;
  defaultDriverUserId?: number | null;
  defaultDriverOther?: string | null;
  defaultEscortUserId?: number | null;
  defaultEscortOther?: string | null;
  remark?: string;
  status?: 0 | 1;
}

/** 更新车辆请求（常态班组整体覆盖，可清空以切换 用户 ↔ OTHER） */
export interface UpdateVehicleRequest {
  plateNo?: string;
  defaultDriverUserId?: number | null;
  defaultDriverOther?: string | null;
  defaultEscortUserId?: number | null;
  defaultEscortOther?: string | null;
  remark?: string;
}

/** 车辆查询参数 */
export interface VehicleQueryParams {
  keyword?: string;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
