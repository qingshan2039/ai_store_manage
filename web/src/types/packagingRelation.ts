/* 包装关系模块类型（对齐 OpenAPI 契约） */
export interface PackagingRelation {
  id: number;
  parentLevelId: number;
  parentLevelName?: string | null;
  childLevelId: number;
  childLevelName?: string | null;
  childQty: number;
  isFixedQty: 0 | 1;
  tareWeight?: number | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

export interface PackagingRelationSummary {
  id: number;
  parentLevelId: number;
  parentLevelName?: string | null;
  childLevelId: number;
  childLevelName?: string | null;
  childQty: number;
  isFixedQty: 0 | 1;
  status: 0 | 1;
  createdAt: string;
}

export interface PackagingRelationListResponse {
  items: PackagingRelationSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CreatePackagingRelationRequest {
  parentLevelId: number;
  childLevelId: number;
  childQty: number;
  isFixedQty?: 0 | 1;
  tareWeight?: number;
  status?: 0 | 1;
}

export interface UpdatePackagingRelationRequest {
  childQty?: number;
  isFixedQty?: 0 | 1;
  tareWeight?: number;
}

export interface PackagingRelationQueryParams {
  parentLevelId?: number;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
