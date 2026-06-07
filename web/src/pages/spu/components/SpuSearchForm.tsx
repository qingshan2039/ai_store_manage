/* ========================================
   SPU 列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { SpuQueryParams } from '@/types/spu';
import type { CategoryOption } from '../SpuListPage';

interface Props {
  form: FormInstance<any>;
  onSearch: (values: SpuQueryParams) => void;
  onReset: () => void;
  categoryOptions: CategoryOption[];
}

const SpuSearchForm: React.FC<Props> = ({ form, onSearch, onReset, categoryOptions }) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="keyword" label="关键词">
        <Input placeholder="名称/编码" allowClear />
      </Form.Item>
      <Form.Item name="categoryCode" label="品类">
        <Select placeholder="请选择品类" options={categoryOptions} allowClear showSearch optionFilterProp="label" />
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
      </Form.Item>
    </SearchForm>
  );
};

export default SpuSearchForm;
