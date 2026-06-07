/* 条码模块类型（对齐 OpenAPI 契约） */
export type BarcodeType = 'EAN13' | 'ITF14' | 'SSCC' | 'OTHER';

export interface Barcode {
  id: number;
  levelId: number;
  levelName?: string | null;
  barcode: string;
  barcodeType: BarcodeType;
  isPrimary: 0 | 1;
  validFrom?: string | null;
  validTo?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

export interface BarcodeSummary {
  id: number;
  levelId: number;
  levelName?: string | null;
  barcode: string;
  barcodeType: BarcodeType;
  isPrimary: 0 | 1;
  status: 0 | 1;
  createdAt: string;
}

export interface BarcodeListResponse {
  items: BarcodeSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CreateBarcodeRequest {
  levelId: number;
  barcode: string;
  barcodeType: BarcodeType;
  isPrimary?: 0 | 1;
  validFrom?: string;
  validTo?: string;
  status?: 0 | 1;
}

export interface UpdateBarcodeRequest {
  barcodeType?: BarcodeType;
  isPrimary?: 0 | 1;
  validFrom?: string;
  validTo?: string;
}

export interface BarcodeQueryParams {
  keyword?: string;
  levelId?: number;
  status?: 0 | 1;
  page?: number;
  pageSize?: number;
}
