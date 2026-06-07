/* 库位模块类型（对齐 OpenAPI 契约） */
export interface Location {
  id: number;
  warehouseId: number;
  warehouseName?: string | null;
  zoneId?: number | null;
  zoneName?: string | null;
  code: string;
  locType?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

export interface LocationSummary {
  id: number;
  warehouseId: number;
  warehouseName?: string | null;
  zoneName?: string | null;
  code: string;
  locType?: string | null;
  status: 0 | 1;
  createdAt: string;
}

export interface LocationListResponse {
  items: LocationSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CreateLocationRequest {
  warehouseId: number;
  zoneId?: number;
  code: string;
  locType?: string;
  status?: 0 | 1;
}

export interface UpdateLocationRequest {
  code?: string;
  zoneId?: number;
  locType?: string;
}

export interface LocationQueryParams {
  keyword?: string;
  warehouseId?: number;
  zoneId?: number;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
