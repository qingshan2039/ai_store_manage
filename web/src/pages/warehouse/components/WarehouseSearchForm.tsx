/* ========================================
   仓库列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS, WAREHOUSE_TYPE_OPTIONS } from '@/constants/enums';
import type { WarehouseQueryParams } from '@/types/warehouse';

interface WarehouseSearchFormProps {
  form: FormInstance<any>;
  onSearch: (values: WarehouseQueryParams) => void;
  onReset: () => void;
}

const WarehouseSearchForm: React.FC<WarehouseSearchFormProps> = ({ form, onSearch, onReset }) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="keyword" label="关键词">
        <Input placeholder="名称/编码" allowClear />
      </Form.Item>
      <Form.Item name="type" label="类型">
        <Select placeholder="请选择类型" options={WAREHOUSE_TYPE_OPTIONS} allowClear />
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
      </Form.Item>
    </SearchForm>
  );
};

export default WarehouseSearchForm;
