/* ========================================
   SKU 列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS, ITEM_TYPE_OPTIONS } from '@/constants/enums';
import type { SkuQueryParams } from '@/types/sku';
import type { SpuOption } from '../SkuListPage';

interface Props {
  form: FormInstance<any>;
  onSearch: (values: SkuQueryParams) => void;
  onReset: () => void;
  spuOptions: SpuOption[];
}

const SkuSearchForm: React.FC<Props> = ({ form, onSearch, onReset, spuOptions }) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={4}>
      <Form.Item name="keyword" label="关键词">
        <Input placeholder="名称/编码" allowClear />
      </Form.Item>
      <Form.Item name="spuId" label="所属 SPU">
        <Select placeholder="请选择 SPU" options={spuOptions} allowClear showSearch optionFilterProp="label" />
      </Form.Item>
      <Form.Item name="itemType" label="阶段">
        <Select placeholder="请选择阶段" options={ITEM_TYPE_OPTIONS} allowClear />
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
      </Form.Item>
    </SearchForm>
  );
};

export default SkuSearchForm;
