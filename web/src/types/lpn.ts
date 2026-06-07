/* 托盘实例模块类型（对齐 OpenAPI 契约） */
export type LpnStatus = 'IN_STOCK' | 'IN_TRANSIT' | 'EMPTY';

export interface Lpn {
  id: number;
  lpnCode: string;
  palletTypeId: number;
  palletTypeName?: string | null;
  warehouseId: number;
  warehouseName?: string | null;
  locationId?: number | null;
  locationCode?: string | null;
  status: LpnStatus;
  grossWeight?: number | null;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

export interface LpnSummary {
  id: number;
  lpnCode: string;
  palletTypeName?: string | null;
  warehouseName?: string | null;
  locationCode?: string | null;
  status: LpnStatus;
  createdAt: string;
}

export interface LpnListResponse {
  items: LpnSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CreateLpnRequest {
  lpnCode: string;
  palletTypeId: number;
  warehouseId: number;
  locationId?: number;
  status?: LpnStatus;
  grossWeight?: number;
}

export interface UpdateLpnRequest {
  locationId?: number;
  grossWeight?: number;
}

export interface UpdateLpnStatusRequest {
  status: LpnStatus;
}

export interface LpnQueryParams {
  keyword?: string;
  warehouseId?: number;
  status?: LpnStatus;
  page?: number;
  pageSize?: number;
}
