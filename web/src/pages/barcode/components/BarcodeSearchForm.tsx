/* 条码搜索表单 */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { BarcodeQueryParams } from '@/types/barcode';
import type { LevelOption } from '../BarcodeListPage';

interface Props {
  form: FormInstance<any>;
  onSearch: (values: BarcodeQueryParams) => void;
  onReset: () => void;
  levelOptions: LevelOption[];
}

const BarcodeSearchForm: React.FC<Props> = ({ form, onSearch, onReset, levelOptions }) => (
  <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
    <Form.Item name="keyword" label="条码">
      <Input placeholder="条码模糊查询" allowClear />
    </Form.Item>
    <Form.Item name="levelId" label="所属包装层">
      <Select placeholder="请选择包装层" options={levelOptions} allowClear showSearch optionFilterProp="label" />
    </Form.Item>
    <Form.Item name="status" label="状态">
      <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
    </Form.Item>
  </SearchForm>
);

export default BarcodeSearchForm;
