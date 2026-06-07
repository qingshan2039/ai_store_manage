/* 库位搜索表单 */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { LocationQueryParams } from '@/types/location';
import type { Option } from '../LocationListPage';

interface Props {
  form: FormInstance<any>;
  onSearch: (values: LocationQueryParams) => void;
  onReset: () => void;
  warehouseOptions: Option[];
}

const LocationSearchForm: React.FC<Props> = ({ form, onSearch, onReset, warehouseOptions }) => (
  <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
    <Form.Item name="keyword" label="库位编码">
      <Input placeholder="编码模糊查询" allowClear />
    </Form.Item>
    <Form.Item name="warehouseId" label="所属仓库">
      <Select placeholder="请选择仓库" options={warehouseOptions} allowClear showSearch optionFilterProp="label" />
    </Form.Item>
    <Form.Item name="status" label="状态">
      <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
    </Form.Item>
  </SearchForm>
);

export default LocationSearchForm;
