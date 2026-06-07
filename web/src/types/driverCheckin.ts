/* ========================================
   司机打卡模块类型（对齐 OpenAPI 契约）
   ======================================== */

/** 出勤状态（对齐契约 CheckinStatus） */
export type CheckinStatus = 'NORMAL' | 'LATE' | 'ABSENT' | 'LEAVE';

/** 打卡详情（对齐 DriverCheckin schema） */
export interface DriverCheckin {
  id: number;
  driverUserId?: number | null;
  driverOther?: string | null;
  driverName?: string | null;
  vehicleId?: number | null;
  vehiclePlateNo?: string | null;
  escortUserId?: number | null;
  escortOther?: string | null;
  escortName?: string | null;
  checkinDate: string;
  clockInAt?: string | null;
  clockOutAt?: string | null;
  checkinStatus: CheckinStatus;
  remark?: string | null;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 打卡列表项（对齐 DriverCheckinSummary schema） */
export interface DriverCheckinSummary {
  id: number;
  driverUserId?: number | null;
  driverName?: string | null;
  vehicleId?: number | null;
  vehiclePlateNo?: string | null;
  escortName?: string | null;
  checkinDate: string;
  clockInAt?: string | null;
  clockOutAt?: string | null;
  checkinStatus: CheckinStatus;
}

/** 打卡列表分页响应 */
export interface DriverCheckinListResponse {
  items: DriverCheckinSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建打卡请求 */
export interface CreateDriverCheckinRequest {
  driverUserId?: number | null;
  driverOther?: string | null;
  vehicleId?: number | null;
  escortUserId?: number | null;
  escortOther?: string | null;
  checkinDate: string;
  clockInAt?: string | null;
  clockOutAt?: string | null;
  checkinStatus?: CheckinStatus;
  remark?: string;
}

/** 更新打卡请求 */
export interface UpdateDriverCheckinRequest {
  driverUserId?: number | null;
  driverOther?: string | null;
  vehicleId?: number | null;
  escortUserId?: number | null;
  escortOther?: string | null;
  checkinDate?: string;
  clockInAt?: string | null;
  clockOutAt?: string | null;
  checkinStatus?: CheckinStatus;
  remark?: string;
}

/** 打卡查询参数 */
export interface DriverCheckinQueryParams {
  driverUserId?: number;
  vehicleId?: number;
  checkinStatus?: CheckinStatus;
  checkinDateStart?: string;
  checkinDateEnd?: string;
  page?: number;
  pageSize?: number;
}
