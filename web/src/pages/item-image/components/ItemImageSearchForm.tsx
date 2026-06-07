/* 物料图片搜索表单 */
import React from 'react';
import { Form, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { ItemImageQueryParams } from '@/types/itemImage';
import type { SkuOption } from '../ItemImageListPage';

interface Props {
  form: FormInstance<any>;
  onSearch: (values: ItemImageQueryParams) => void;
  onReset: () => void;
  skuOptions: SkuOption[];
}

const ItemImageSearchForm: React.FC<Props> = ({ form, onSearch, onReset, skuOptions }) => (
  <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
    <Form.Item name="skuId" label="所属 SKU">
      <Select placeholder="请选择 SKU" options={skuOptions} allowClear showSearch optionFilterProp="label" />
    </Form.Item>
    <Form.Item name="status" label="状态">
      <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
    </Form.Item>
  </SearchForm>
);

export default ItemImageSearchForm;
