/* ========================================
   司机每日打卡列表页面（运输管理）
   ======================================== */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Form, Space, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { driverCheckinApi } from '@/api/driverCheckin';
import { vehicleApi } from '@/api/vehicle';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { useCrewOptions } from '@/hooks/useCrewOptions';
import type { StaffOption } from '@/hooks/useCrewOptions';
import { CHECKIN_STATUS_MAP } from '@/constants/enums';
import type { DriverCheckinSummary, DriverCheckinQueryParams, DriverCheckin } from '@/types/driverCheckin';
import DriverCheckinSearchForm from './components/DriverCheckinSearchForm';
import DriverCheckinFormModal from './components/DriverCheckinFormModal';
import { formatDateTime } from '@/utils/format';

const DriverCheckinListPage: React.FC = () => {
  const [searchForm] = Form.useForm<DriverCheckinQueryParams>();
  const [vehicleOptions, setVehicleOptions] = useState<StaffOption[]>([]);
  const { driverOptions, escortOptions } = useCrewOptions();

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<DriverCheckinSummary, DriverCheckinQueryParams>({
      fetchFn: driverCheckinApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<DriverCheckin>();

  useEffect(() => {
    vehicleApi
      .list({ status: 1, pageSize: 100 })
      .then((res) => setVehicleOptions(res.data.items.map((v) => ({ label: v.plateNo, value: v.id }))))
      .catch(() => {});
  }, []);

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await driverCheckinApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {
      /* 已提示 */
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await driverCheckinApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {
      /* 已提示 */
    }
  };

  const columns: TableProps<DriverCheckinSummary>['columns'] = [
    { title: '打卡日期', dataIndex: 'checkinDate', key: 'checkinDate', width: 120 },
    { title: '司机', dataIndex: 'driverName', key: 'driverName', width: 140, render: (v) => v || '-' },
    { title: '车辆', dataIndex: 'vehiclePlateNo', key: 'vehiclePlateNo', width: 120, render: (v) => v || '-' },
    { title: '跟车员', dataIndex: 'escortName', key: 'escortName', width: 140, render: (v) => v || '-' },
    { title: '上班', dataIndex: 'clockInAt', key: 'clockInAt', width: 150, render: (v) => (v ? formatDateTime(v) : '-') },
    { title: '下班', dataIndex: 'clockOutAt', key: 'clockOutAt', width: 150, render: (v) => (v ? formatDateTime(v) : '-') },
    {
      title: '出勤',
      dataIndex: 'checkinStatus',
      key: 'checkinStatus',
      width: 90,
      render: (s: string) => {
        const meta = CHECKIN_STATUS_MAP[s];
        return meta ? <Tag color={meta.color}>{meta.label}</Tag> : s;
      },
    },
    {
      title: '操作',
      key: 'action',
      width: 130,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleEdit(record.id)}>编辑</a>
          <ConfirmAction
            title="确定要删除该打卡记录吗？"
            onConfirm={() => handleDelete(record.id)}
            type="link"
            danger
            style={{ padding: 0, height: 'auto' }}
          >
            删除
          </ConfirmAction>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer title="司机打卡">
      <DriverCheckinSearchForm
        form={searchForm}
        onSearch={onSearch}
        onReset={onReset}
        driverOptions={driverOptions}
        vehicleOptions={vehicleOptions}
      />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建打卡
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1100 }}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (t) => `共 ${t} 条记录`,
            onChange: onPageChange,
          }}
        />
      </Card>

      <DriverCheckinFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        driverOptions={driverOptions}
        escortOptions={escortOptions}
        vehicleOptions={vehicleOptions}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default DriverCheckinListPage;
