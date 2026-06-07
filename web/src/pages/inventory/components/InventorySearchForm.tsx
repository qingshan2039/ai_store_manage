/* 库存查询搜索表单 */
import React from 'react';
import { Form, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import type { InventoryQueryParams } from '@/types/inventory';
import type { Option } from '../InventoryListPage';

interface Props {
  form: FormInstance<any>;
  onSearch: (values: InventoryQueryParams) => void;
  onReset: () => void;
  skuOptions: Option[];
}

const InventorySearchForm: React.FC<Props> = ({ form, onSearch, onReset, skuOptions }) => (
  <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
    <Form.Item name="skuId" label="所属 SKU">
      <Select placeholder="请选择 SKU" options={skuOptions} allowClear showSearch optionFilterProp="label" />
    </Form.Item>
  </SearchForm>
);

export default InventorySearchForm;
