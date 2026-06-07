/* ========================================
   打油记录列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import type { FuelRecordQueryParams } from '@/types/fuelRecord';
import type { StaffOption } from '@/hooks/useCrewOptions';

interface FuelRecordSearchFormProps {
  form: FormInstance<FuelRecordQueryParams>;
  onSearch: (values: FuelRecordQueryParams) => void;
  onReset: () => void;
  vehicleOptions: StaffOption[];
}

const FuelRecordSearchForm: React.FC<FuelRecordSearchFormProps> = ({ form, onSearch, onReset, vehicleOptions }) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="vehicleId" label="车辆">
        <Select placeholder="按车辆筛选" options={vehicleOptions} allowClear showSearch optionFilterProp="label" />
      </Form.Item>
    </SearchForm>
  );
};

export default FuelRecordSearchForm;
