/* ========================================
   用户模块类型（对齐 OpenAPI 契约）
   后续应由 openapi-typescript 自动生成，
   当前手动编写以保证可运行。
   ======================================== */

/** 用户详情（对齐 User schema） */
export interface User {
  id: number;
  employeeNo: string;
  username: string;
  name: string;
  nickname?: string | null;
  avatar?: string | null;
  gender: 0 | 1 | 2;
  phoneNumber: string;
  email?: string | null;
  jobTitle?: string | null;
  departmentId?: number | null;
  departmentName?: string | null;
  hidePhoneNumber: boolean;
  hideName: boolean;
  remark?: string | null;
  status: 0 | 1;
  createdAt: string;
  updatedAt: string;
  createdBy?: number | null;
  updatedBy?: number | null;
}

/** 用户列表项（对齐 UserSummary schema） */
export interface UserSummary {
  id: number;
  employeeNo: string;
  username: string;
  name: string;
  nickname?: string | null;
  avatar?: string | null;
  gender: 0 | 1 | 2;
  phoneNumber: string;
  email?: string | null;
  jobTitle?: string | null;
  departmentId?: number | null;
  departmentName?: string | null;
  status: 0 | 1;
  createdAt: string;
}

/** 用户列表分页响应 */
export interface UserListResponse {
  items: UserSummary[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/** 创建用户请求 */
export interface CreateUserRequest {
  employeeNo: string;
  username: string;
  password: string;
  name: string;
  nickname?: string;
  gender?: 0 | 1 | 2;
  phoneNumber: string;
  email?: string;
  jobTitle?: string;
  departmentId?: number;
  hidePhoneNumber?: boolean;
  hideName?: boolean;
  remark?: string;
  status?: 0 | 1;
}

/** 更新用户请求 */
export interface UpdateUserRequest {
  name?: string;
  nickname?: string;
  avatar?: string;
  gender?: 0 | 1 | 2;
  phoneNumber?: string;
  email?: string;
  jobTitle?: string;
  departmentId?: number;
  hidePhoneNumber?: boolean;
  hideName?: boolean;
  remark?: string;
}

/** 状态变更请求 */
export interface UpdateUserStatusRequest {
  status: 0 | 1;
}

/** 密码重置请求 */
export interface ResetUserPasswordRequest {
  newPassword: string;
}

/** 用户查询参数 */
export interface UserQueryParams {
  keyword?: string;
  employeeNo?: string;
  name?: string;
  phoneNumber?: string;
  status?: 0 | 1;
  departmentId?: number;
  jobTitle?: string;
  gender?: 0 | 1 | 2;
  createdAtStart?: string;
  createdAtEnd?: string;
  page?: number;
  pageSize?: number;
}
