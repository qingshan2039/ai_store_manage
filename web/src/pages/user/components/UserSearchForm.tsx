/* ========================================
   用户列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Input, Select, DatePicker } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { USER_STATUS_OPTIONS, GENDER_OPTIONS } from '@/constants/enums';
import type { UserQueryParams } from '@/types/user';

const { RangePicker } = DatePicker;

interface UserSearchFormProps {
  form: FormInstance<any>;
  onSearch: (values: UserQueryParams) => void;
  onReset: () => void;
}

const UserSearchForm: React.FC<UserSearchFormProps> = ({ form, onSearch, onReset }) => {
  const handleSearch = (values: any) => {
    const formattedValues: any = { ...values };
    
    // 处理日期范围
    if (values.createdAtRange && values.createdAtRange.length === 2) {
      formattedValues.createdAtStart = values.createdAtRange[0].format('YYYY-MM-DD 00:00:00');
      formattedValues.createdAtEnd = values.createdAtRange[1].format('YYYY-MM-DD 23:59:59');
    }
    delete formattedValues['createdAtRange'];

    onSearch(formattedValues as UserQueryParams);
  };

  return (
    <SearchForm form={form} onSearch={handleSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="keyword" label="关键词">
        <Input placeholder="姓名/工号/手机号" allowClear />
      </Form.Item>
      <Form.Item name="employeeNo" label="工号">
        <Input placeholder="精确匹配工号" allowClear />
      </Form.Item>
      <Form.Item name="name" label="姓名">
        <Input placeholder="模糊匹配姓名" allowClear />
      </Form.Item>
      <Form.Item name="phoneNumber" label="手机号">
        <Input placeholder="精确匹配手机号" allowClear />
      </Form.Item>
      <Form.Item name="status" label="状态">
        <Select placeholder="请选择状态" options={USER_STATUS_OPTIONS} allowClear />
      </Form.Item>
      <Form.Item name="gender" label="性别">
        <Select placeholder="请选择性别" options={GENDER_OPTIONS} allowClear />
      </Form.Item>
      <Form.Item name="jobTitle" label="职位">
        <Input placeholder="模糊匹配职位" allowClear />
      </Form.Item>
      <Form.Item name="createdAtRange" label="创建时间">
        <RangePicker style={{ width: '100%' }} />
      </Form.Item>
    </SearchForm>
  );
};

export default UserSearchForm;
