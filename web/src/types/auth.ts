/* ========================================
   认证相关类型
   ======================================== */

/** 登录请求 */
export interface LoginRequest {
  username: string;
  password: string;
}

/** 登录用户信息 */
export interface UserInfo {
  id: number;
  username: string;
  name: string;
  avatar?: string | null;
  permissions?: string[];
}
