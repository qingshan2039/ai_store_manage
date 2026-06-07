/* 物料图片模块类型（对齐 OpenAPI 契约） */
export interface ItemImage {
  id: number;
  spuId?: number | null;
  skuId?: number | null;
  levelId?: number | null;
  imageUrl: string;
  imageType?: string | null;
  sortOrder: number;
  isPrimary: 0 | 1;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

export interface ItemImageSummary {
  id: number;
  spuId?: number | null;
  skuId?: number | null;
  levelId?: number | null;
  imageUrl: string;
  imageType?: string | null;
  sortOrder: number;
  isPrimary: 0 | 1;
  status: 0 | 1;
  createdAt: string;
}

export interface ItemImageListResponse {
  items: ItemImageSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CreateItemImageRequest {
  spuId?: number;
  skuId?: number;
  levelId?: number;
  imageUrl: string;
  imageType?: string;
  sortOrder?: number;
  isPrimary?: 0 | 1;
  status?: 0 | 1;
}

export interface UpdateItemImageRequest {
  imageUrl?: string;
  imageType?: string;
  sortOrder?: number;
  isPrimary?: 0 | 1;
}

export interface ItemImageQueryParams {
  spuId?: number;
  skuId?: number;
  levelId?: number;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
