/* ========================================
   业务枚举与映射
   ======================================== */

/** 用户状态 */
export const USER_STATUS = {
  DISABLED: 0,
  ENABLED: 1,
} as const;

export const USER_STATUS_MAP: Record<number, { label: string; color: string }> = {
  [USER_STATUS.DISABLED]: { label: '禁用', color: 'red' },
  [USER_STATUS.ENABLED]: { label: '启用', color: 'green' },
};

export const USER_STATUS_OPTIONS = [
  { label: '启用', value: USER_STATUS.ENABLED },
  { label: '禁用', value: USER_STATUS.DISABLED },
];

/** 性别 */
export const GENDER = {
  UNKNOWN: 0,
  MALE: 1,
  FEMALE: 2,
} as const;

export const GENDER_MAP: Record<number, string> = {
  [GENDER.UNKNOWN]: '未知',
  [GENDER.MALE]: '男',
  [GENDER.FEMALE]: '女',
};

export const GENDER_OPTIONS = [
  { label: '未知', value: GENDER.UNKNOWN },
  { label: '男', value: GENDER.MALE },
  { label: '女', value: GENDER.FEMALE },
];

/** 部门状态 */
export const DEPARTMENT_STATUS = {
  DISABLED: 0,
  ENABLED: 1,
} as const;

export const DEPARTMENT_STATUS_OPTIONS = [
  { label: '启用', value: DEPARTMENT_STATUS.ENABLED },
  { label: '禁用', value: DEPARTMENT_STATUS.DISABLED },
];

/** 部门类型（对齐契约 DepartmentType） */
export const DEPARTMENT_TYPE_MAP: Record<string, string> = {
  WAREHOUSE: '仓管',
  TRANSPORT: '运输',
  SALES: '销售',
  PRODUCTION: '生产',
  OFFICE: '办公',
  HR: '人事',
  FINANCE: '财务',
  MANAGEMENT: '管理（管理层）',
};

export const DEPARTMENT_TYPE_OPTIONS = [
  { label: '仓管', value: 'WAREHOUSE' },
  { label: '运输', value: 'TRANSPORT' },
  { label: '销售', value: 'SALES' },
  { label: '生产', value: 'PRODUCTION' },
  { label: '办公', value: 'OFFICE' },
  { label: '人事', value: 'HR' },
  { label: '财务', value: 'FINANCE' },
  { label: '管理（管理层）', value: 'MANAGEMENT' },
];
