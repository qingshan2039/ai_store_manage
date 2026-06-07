/* ========================================
   部门列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Input, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { DEPARTMENT_STATUS_OPTIONS, DEPARTMENT_TYPE_OPTIONS } from '@/constants/enums';
import type { DepartmentQueryParams } from '@/types/department';

interface DepartmentSearchFormProps {
  form: FormInstance<any>;
  onSearch: (values: DepartmentQueryParams) => void;
  onReset: () => void;
}

const DepartmentSearchForm: React.FC<DepartmentSearchFormProps> = ({ form, onSearch, onReset }) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="keyword" label="关键词">
        <Input placeholder="名称/编码" allowClear />
      </Form.Item>
      <Form.Item name="type" label="类型">
        <Select placeholder="请选择类型" options={DEPARTMENT_TYPE_OPTIONS} allowClear />
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态" options={DEPARTMENT_STATUS_OPTIONS} allowClear />
      </Form.Item>
    </SearchForm>
  );
};

export default DepartmentSearchForm;
