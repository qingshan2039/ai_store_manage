/* ========================================
   打油记录模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 打油记录详情（对齐 FuelRecord schema） */
export interface FuelRecord {
  id: number;
  vehicleId: number;
  vehiclePlateNo?: string | null;
  driverUserId?: number | null;
  driverName?: string | null;
  fuelDate: string;
  liters?: number | null;
  amount?: number | null;
  unitPrice?: number | null;
  odometer?: number | null;
  images?: string[] | null;
  remark?: string | null;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 打油记录列表项（对齐 FuelRecordSummary schema） */
export interface FuelRecordSummary {
  id: number;
  vehicleId: number;
  vehiclePlateNo?: string | null;
  driverName?: string | null;
  fuelDate: string;
  liters?: number | null;
  amount?: number | null;
  imageCount: number;
  createdAt: string;
}

/** 打油记录列表分页响应 */
export interface FuelRecordListResponse {
  items: FuelRecordSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建打油记录请求 */
export interface CreateFuelRecordRequest {
  vehicleId: number;
  driverUserId?: number | null;
  fuelDate: string;
  liters?: number | null;
  amount?: number | null;
  unitPrice?: number | null;
  odometer?: number | null;
  images?: string[];
  remark?: string;
}

/** 更新打油记录请求 */
export interface UpdateFuelRecordRequest {
  vehicleId?: number;
  driverUserId?: number | null;
  fuelDate?: string;
  liters?: number | null;
  amount?: number | null;
  unitPrice?: number | null;
  odometer?: number | null;
  images?: string[];
  remark?: string;
}

/** 打油记录查询参数 */
export interface FuelRecordQueryParams {
  vehicleId?: number;
  fuelDateStart?: string;
  fuelDateEnd?: string;
  page?: number;
  pageSize?: number;
}
