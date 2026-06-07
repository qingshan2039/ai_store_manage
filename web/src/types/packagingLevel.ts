/* 包装层级模块类型（对齐 OpenAPI 契约） */
export interface PackagingLevel {
  id: number;
  skuId: number;
  skuCode?: string | null;
  skuName?: string | null;
  levelName: string;
  levelSeq: number;
  unitCode: string;
  length?: number | null;
  width?: number | null;
  height?: number | null;
  netWeight?: number | null;
  grossWeight?: number | null;
  isBaseUnit: 0 | 1;
  isSellable: 0 | 1;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

export interface PackagingLevelSummary {
  id: number;
  skuId: number;
  skuName?: string | null;
  levelName: string;
  levelSeq: number;
  unitCode: string;
  isBaseUnit: 0 | 1;
  isSellable: 0 | 1;
  status: 0 | 1;
  createdAt: string;
}

export interface PackagingLevelListResponse {
  items: PackagingLevelSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CreatePackagingLevelRequest {
  skuId: number;
  levelName: string;
  levelSeq: number;
  unitCode: string;
  length?: number;
  width?: number;
  height?: number;
  netWeight?: number;
  grossWeight?: number;
  isBaseUnit?: 0 | 1;
  isSellable?: 0 | 1;
  status?: 0 | 1;
}

export interface UpdatePackagingLevelRequest {
  levelName?: string;
  unitCode?: string;
  length?: number;
  width?: number;
  height?: number;
  netWeight?: number;
  grossWeight?: number;
  isBaseUnit?: 0 | 1;
  isSellable?: 0 | 1;
}

export interface PackagingLevelQueryParams {
  skuId?: number;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
