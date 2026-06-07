/* 计量换算模块类型（对齐 OpenAPI 契约） */
export interface UnitConversion {
  id: number;
  skuId: number;
  skuCode?: string | null;
  skuName?: string | null;
  fromUnit: string;
  toUnit: string;
  factor: number;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

export interface UnitConversionSummary {
  id: number;
  skuId: number;
  skuName?: string | null;
  fromUnit: string;
  toUnit: string;
  factor: number;
  status: 0 | 1;
  createdAt: string;
}

export interface UnitConversionListResponse {
  items: UnitConversionSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CreateUnitConversionRequest {
  skuId: number;
  fromUnit: string;
  toUnit: string;
  factor: number;
  status?: 0 | 1;
}

export interface UpdateUnitConversionRequest {
  factor?: number;
}

export interface UnitConversionQueryParams {
  skuId?: number;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
