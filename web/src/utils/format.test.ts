import { describe, it, expect } from 'vitest';
import { formatDateTime, maskPhone } from './format';

describe('maskPhone', () => {
  it('11 位手机号脱敏为 前3****后4', () => {
    expect(maskPhone('13800138000')).toBe('138****8000');
  });
  it('空值返回 -', () => {
    expect(maskPhone(null)).toBe('-');
    expect(maskPhone(undefined)).toBe('-');
  });
  it('过短(<7 位)原样返回', () => {
    expect(maskPhone('123')).toBe('123');
  });
});

describe('formatDateTime', () => {
  it('空值返回 -', () => {
    expect(formatDateTime(null)).toBe('-');
    expect(formatDateTime('')).toBe('-');
  });
  it('格式化 ISO 时间为 YYYY-MM-DD HH:mm:ss', () => {
    expect(formatDateTime('2026-04-22T10:00:00')).toBe('2026-04-22 10:00:00');
  });
});
