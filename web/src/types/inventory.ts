/* 库存模块类型（对齐 OpenAPI 契约） */
export interface Inventory {
  id: number;
  skuId: number;
  skuCode?: string | null;
  skuName?: string | null;
  lpnId?: number | null;
  lpnCode?: string | null;
  locationId?: number | null;
  locationCode?: string | null;
  lotNo?: string | null;
  mfgDate?: string | null;
  expDate?: string | null;
  qtyOnHand: number;
  qtyReserved: number;
  qtyAvailable: number;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

export interface InventorySummaryItem {
  id: number;
  skuId: number;
  skuName?: string | null;
  lpnCode?: string | null;
  locationCode?: string | null;
  lotNo?: string | null;
  qtyOnHand: number;
  qtyReserved: number;
  qtyAvailable: number;
  createdAt: string;
}

export interface InventoryListResponse {
  items: InventorySummaryItem[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

export interface CreateInventoryRequest {
  skuId: number;
  lpnId?: number;
  locationId?: number;
  lotNo?: string;
  mfgDate?: string;
  expDate?: string;
  qtyOnHand: number;
  qtyReserved?: number;
}

export interface UpdateInventoryRequest {
  lpnId?: number;
  locationId?: number;
  lotNo?: string;
  mfgDate?: string;
  expDate?: string;
  qtyOnHand?: number;
  qtyReserved?: number;
}

export interface InventoryQueryParams {
  skuId?: number;
  lpnId?: number;
  locationId?: number;
  page?: number;
  pageSize?: number;
}

/** 每托明细（整托/尾托） */
export interface InventoryPallet {
  lpnId: number;
  lpnCode?: string | null;
  qty: number;
  fullPallet?: boolean | null;
}

/** 库存统计（需求②） */
export interface InventorySummary {
  skuId: number;
  skuName?: string | null;
  totalQty: number;
  totalReserved: number;
  totalAvailable: number;
  palletCount: number;
  recordCount: number;
  standardPalletQty?: number | null;
  pallets: InventoryPallet[];
}
