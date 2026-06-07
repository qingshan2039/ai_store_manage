/* ========================================
   司机打卡列表搜索表单
   ======================================== */
import React from 'react';
import { Form, Select } from 'antd';
import type { FormInstance } from 'antd';
import SearchForm from '@/components/SearchForm';
import { CHECKIN_STATUS_OPTIONS } from '@/constants/enums';
import type { DriverCheckinQueryParams } from '@/types/driverCheckin';
import type { StaffOption } from '@/hooks/useCrewOptions';

interface DriverCheckinSearchFormProps {
  form: FormInstance<DriverCheckinQueryParams>;
  onSearch: (values: DriverCheckinQueryParams) => void;
  onReset: () => void;
  driverOptions: StaffOption[];
  vehicleOptions: StaffOption[];
}

const DriverCheckinSearchForm: React.FC<DriverCheckinSearchFormProps> = ({
  form,
  onSearch,
  onReset,
  driverOptions,
  vehicleOptions,
}) => {
  return (
    <SearchForm form={form} onSearch={onSearch} onReset={onReset} defaultVisibleCount={3}>
      <Form.Item name="driverUserId" label="司机">
        <Select placeholder="按司机筛选" options={driverOptions} allowClear showSearch optionFilterProp="label" />
      </Form.Item>
      <Form.Item name="vehicleId" label="车辆">
        <Select placeholder="按车辆筛选" options={vehicleOptions} allowClear showSearch optionFilterProp="label" />
      </Form.Item>
      <Form.Item name="checkinStatus" label="出勤状态">
        <Select placeholder="按出勤状态筛选" options={CHECKIN_STATUS_OPTIONS} allowClear />
      </Form.Item>
    </SearchForm>
  );
};

export default DriverCheckinSearchForm;
