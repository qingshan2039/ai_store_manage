/* ========================================
   班组人员选择器（用户 或 OTHER 替补）
   复合 Form.Item 值：{ userId?, other? }。
   选中在册用户时 userId 有值；选“其他”时填写替补名 other（用于缺席替补）。
   ======================================== */
import React from 'react';
import { Select, Input } from 'antd';
import type { StaffOption } from '@/hooks/useCrewOptions';

export interface StaffValue {
  userId?: number | null;
  other?: string | null;
}

interface StaffPickerProps {
  value?: StaffValue;
  onChange?: (v: StaffValue) => void;
  options: StaffOption[];
  placeholder?: string;
  otherPlaceholder?: string;
}

/** 特殊值表示“其他（替补）”；用户 id 均为正整数，-1 不会冲突 */
const OTHER = -1;

const StaffPicker: React.FC<StaffPickerProps> = ({
  value = {},
  onChange,
  options,
  placeholder = '请选择',
  otherPlaceholder = '替补姓名 / 说明',
}) => {
  const isOther = value.userId == null && value.other != null;
  const selectValue = value.userId != null ? value.userId : isOther ? OTHER : undefined;

  const handleSelect = (v: number | undefined) => {
    if (v === undefined) onChange?.({ userId: null, other: null });
    else if (v === OTHER) onChange?.({ userId: null, other: value.other ?? '' });
    else onChange?.({ userId: v, other: null });
  };

  return (
    <div>
      <Select
        style={{ width: '100%' }}
        value={selectValue}
        onChange={handleSelect}
        placeholder={placeholder}
        allowClear
        showSearch
        optionFilterProp="label"
        options={[...options, { label: '其他（替补 / 缺席）', value: OTHER }]}
      />
      {selectValue === OTHER && (
        <Input
          style={{ marginTop: 8 }}
          value={value.other ?? ''}
          onChange={(e) => onChange?.({ userId: null, other: e.target.value })}
          placeholder={otherPlaceholder}
          maxLength={64}
        />
      )}
    </div>
  );
};

export default StaffPicker;
