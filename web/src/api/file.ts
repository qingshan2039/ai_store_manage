/* ========================================
   文件上传 API（multipart）
   ======================================== */
import request from './request';
import type { UploadFileResponse } from '@/types/file';

export const fileApi = {
  /**
   * 上传文件（图片）— POST /api/files → 201 { url }
   * Content-Type 置 null 以移除实例默认的 application/json，让 axios 依据 FormData 自动带 multipart 边界。
   */
  upload(file: File) {
    const formData = new FormData();
    formData.append('file', file);
    return request.post<UploadFileResponse>('/api/files', formData, {
      headers: { 'Content-Type': null },
    });
  },
};
