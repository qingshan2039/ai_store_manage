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
