/* ========================================
   Axios 实例 + 拦截器
   ======================================== */
import axios from 'axios';
import type { AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '@/stores/useAuthStore';
import type { ErrorResponse } from '@/types/common';

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
});

/** 请求拦截：注入 Token */
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = useAuthStore.getState().token;
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

/** 响应拦截：统一错误处理 */
request.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      const errorData = data as ErrorResponse | undefined;
      const errorMessage = errorData?.message ?? '请求失败，请稍后重试';

      switch (status) {
        case 401:
          useAuthStore.getState().logout();
          window.location.href = '/login';
          break;
        case 403:
          console.error('[API 403]', '没有权限执行此操作');
          break;
        default:
          console.error(`[API ${status}]`, errorMessage);
      }
    } else {
      console.error('[API Network Error]', '网络异常，请检查网络连接');
    }
    return Promise.reject(error.response?.data ?? error);
  },
);

export default request;
