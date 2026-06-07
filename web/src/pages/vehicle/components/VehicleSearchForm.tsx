/* ========================================
   车辆列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { VehicleQueryParams } from '@/types/vehicle';

interface VehicleSearchFormProps {
  form: FormInstance<VehicleQueryParams>;
  onSearch: (values: VehicleQueryParams) => void;
  onReset: () => void;
}

const VehicleSearchForm: React.FC<VehicleSearchFormProps> = ({ form, onSearch, onReset }) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="keyword" label="车牌号">
        <Input placeholder="按车牌号搜索" allowClear />
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
      </Form.Item>
    </SearchForm>
  );
};

export default VehicleSearchForm;
