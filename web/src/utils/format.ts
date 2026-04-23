/* ========================================
   格式化工具函数
   ======================================== */
import dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from '@/constants/config';

/** 格式化日期时间 */
export function formatDateTime(value: string | null | undefined, format: string = DATE_TIME_FORMAT): string {
  if (!value) return '-';
  return dayjs(value).format(format);
}

/** 手机号脱敏 */
export function maskPhone(phone: string | null | undefined): string {
  if (!phone || phone.length < 7) return phone ?? '-';
  return `${phone.slice(0, 3)}****${phone.slice(-4)}`;
}
