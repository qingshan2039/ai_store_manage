/* 包装关系搜索表单 */
import React from 'react';
import { Form, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { STATUS_OPTIONS } from '@/constants/enums';
import type { PackagingRelationQueryParams } from '@/types/packagingRelation';
import type { LevelOption } from '../PackagingRelationListPage';

interface Props {
  form: FormInstance<any>;
  onSearch: (values: PackagingRelationQueryParams) => void;
  onReset: () => void;
  levelOptions: LevelOption[];
}

const PackagingRelationSearchForm: React.FC<Props> = ({ form, onSearch, onReset, levelOptions }) => (
  <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
    <Form.Item name="parentLevelId" label="父层">
      <Select placeholder="请选择父层" options={levelOptions} allowClear showSearch optionFilterProp="label" />
    </Form.Item>
    <Form.Item name="status" label="状态">
      <Select placeholder="请选择状态" options={STATUS_OPTIONS} allowClear />
    </Form.Item>
  </SearchForm>
);

export default PackagingRelationSearchForm;
