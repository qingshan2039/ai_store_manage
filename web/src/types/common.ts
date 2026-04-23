/* ========================================
   通用类型定义
   ======================================== */

/** 分页请求参数 */
export interface PaginationParams {
  page?: number;
  pageSize?: number;
}

/** 分页响应结构 */
export interface PageResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 统一错误响应（对齐 OpenAPI ErrorResponse） */
export interface ErrorResponse {
  code: string;
  message: string;
  details?: FieldError[];
}

/** 字段级错误 */
export interface FieldError {
  field: string;
  message: string;
}

/** 弹窗操作模式 */
export type ModalMode = 'create' | 'edit' | 'view';
