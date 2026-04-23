/* ========================================
   用户模块 API（严格对齐 OpenAPI 契约）
   ======================================== */
import request from './request';
import type {
  User,
  UserListResponse,
  CreateUserRequest,
  UpdateUserRequest,
  UpdateUserStatusRequest,
  ResetUserPasswordRequest,
  UserQueryParams,
} from '@/types/user';

export const userApi = {
  /** 创建用户 — POST /api/users → 201 */
  create(data: CreateUserRequest) {
    return request.post<User>('/api/users', data);
  },

  /** 查询用户列表 — GET /api/users → 200 */
  list(params: UserQueryParams) {
    return request.get<UserListResponse>('/api/users', { params });
  },

  /** 查询用户详情 — GET /api/users/:id → 200 */
  getById(id: number) {
    return request.get<User>(`/api/users/${id}`);
  },

  /** 更新用户 — PUT /api/users/:id → 200 */
  update(id: number, data: UpdateUserRequest) {
    return request.put<User>(`/api/users/${id}`, data);
  },

  /** 删除用户 — DELETE /api/users/:id → 204 */
  delete(id: number) {
    return request.delete(`/api/users/${id}`);
  },

  /** 变更用户状态 — PATCH /api/users/:id/status → 200 */
  updateStatus(id: number, data: UpdateUserStatusRequest) {
    return request.patch<User>(`/api/users/${id}/status`, data);
  },

  /** 重置用户密码 — PUT /api/users/:id/password → 204 */
  resetPassword(id: number, data: ResetUserPasswordRequest) {
    return request.put(`/api/users/${id}/password`, data);
  },
};
