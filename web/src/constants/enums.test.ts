import { describe, it, expect } from 'vitest';
import {
  DEPARTMENT_TYPE_MAP,
  DEPARTMENT_TYPE_OPTIONS,
  DEPARTMENT_STATUS,
  DEPARTMENT_STATUS_OPTIONS,
  CHECKIN_STATUS_MAP,
  CHECKIN_STATUS_OPTIONS,
} from './enums';

describe('部门类型枚举', () => {
  const EXPECTED = ['WAREHOUSE', 'TRANSPORT', 'SALES', 'PRODUCTION', 'OFFICE', 'HR', 'FINANCE', 'MANAGEMENT'];

  it('8 种类型齐全（map 与 options 一致）', () => {
    expect(DEPARTMENT_TYPE_OPTIONS).toHaveLength(8);
    EXPECTED.forEach((t) => {
      expect(DEPARTMENT_TYPE_MAP[t]).toBeTruthy();
      expect(DEPARTMENT_TYPE_OPTIONS.some((o) => o.value === t)).toBe(true);
    });
  });

  it('MANAGEMENT 标注为管理层', () => {
    expect(DEPARTMENT_TYPE_MAP.MANAGEMENT).toContain('管理');
  });
});

describe('部门状态枚举', () => {
  it('0=禁用 1=启用，options 两项', () => {
    expect(DEPARTMENT_STATUS.ENABLED).toBe(1);
    expect(DEPARTMENT_STATUS.DISABLED).toBe(0);
    expect(DEPARTMENT_STATUS_OPTIONS).toHaveLength(2);
  });
});

describe('打卡出勤状态枚举（对齐契约 CheckinStatus）', () => {
  const EXPECTED = ['NORMAL', 'LATE', 'ABSENT', 'LEAVE'];

  it('4 种状态齐全（map 与 options 一致）', () => {
    expect(CHECKIN_STATUS_OPTIONS).toHaveLength(4);
    EXPECTED.forEach((s) => {
      expect(CHECKIN_STATUS_MAP[s]).toBeTruthy();
      expect(CHECKIN_STATUS_OPTIONS.some((o) => o.value === s)).toBe(true);
    });
  });
});
