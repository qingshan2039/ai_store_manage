/* ========================================
   顾客列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { CUSTOMER_STATUS_OPTIONS } from '@/constants/enums';
import type { CustomerQueryParams } from '@/types/customer';

interface CustomerSearchFormProps {
  form: FormInstance<any>;
  onSearch: (values: CustomerQueryParams) => void;
  onReset: () => void;
}

const CustomerSearchForm: React.FC<CustomerSearchFormProps> = ({ form, onSearch, onReset }) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="keyword" label="关键词">
        <Input placeholder="名称/编码/联系人" allowClear />
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态" options={CUSTOMER_STATUS_OPTIONS} allowClear />
      </Form.Item>
    </SearchForm>
  );
};

export default CustomerSearchForm;
