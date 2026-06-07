/* ========================================
   库区列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { ZoneQueryParams } from '@/types/zone';
import type { WarehouseOption } from '../ZoneListPage';

interface ZoneSearchFormProps {
  form: FormInstance<any>;
  onSearch: (values: ZoneQueryParams) => void;
  onReset: () => void;
  warehouseOptions: WarehouseOption[];
}

const ZoneSearchForm: React.FC<ZoneSearchFormProps> = ({ form, onSearch, onReset, warehouseOptions }) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="keyword" label="关键词">
        <Input placeholder="名称/编码" allowClear />
      </Form.Item>
      <Form.Item name="warehouseId" label="所属仓库">
        <Select placeholder="请选择仓库" options={warehouseOptions} allowClear showSearch optionFilterProp="label" />
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
      </Form.Item>
    </SearchForm>
  );
};

export default ZoneSearchForm;
