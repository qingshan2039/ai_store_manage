/* 包装层级搜索表单 */
import React from 'react';
import { Form, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { PackagingLevelQueryParams } from '@/types/packagingLevel';
import type { SkuOption } from '../PackagingLevelListPage';

interface Props {
  form: FormInstance<any>;
  onSearch: (values: PackagingLevelQueryParams) => void;
  onReset: () => void;
  skuOptions: SkuOption[];
}

const PackagingLevelSearchForm: React.FC<Props> = ({ form, onSearch, onReset, skuOptions }) => (
  <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
    <Form.Item name="skuId" label="所属 SKU">
      <Select placeholder="请选择 SKU" options={skuOptions} allowClear showSearch optionFilterProp="label" />
    </Form.Item>
    <Form.Item name="status" label="状态">
      <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
    </Form.Item>
  </SearchForm>
);

export default PackagingLevelSearchForm;
