/* ========================================
   打油记录列表页面（运输管理）
   ======================================== */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Form, Space, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { fuelRecordApi } from '@/api/fuelRecord';
import { vehicleApi } from '@/api/vehicle';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { useCrewOptions } from '@/hooks/useCrewOptions';
import type { StaffOption } from '@/hooks/useCrewOptions';
import type { FuelRecordSummary, FuelRecordQueryParams, FuelRecord } from '@/types/fuelRecord';
import FuelRecordSearchForm from './components/FuelRecordSearchForm';
import FuelRecordFormModal from './components/FuelRecordFormModal';
import { formatDateTime } from '@/utils/format';

const FuelRecordListPage: React.FC = () => {
  const [searchForm] = Form.useForm<FuelRecordQueryParams>();
  const [vehicleOptions, setVehicleOptions] = useState<StaffOption[]>([]);
  const { driverOptions } = useCrewOptions();

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<FuelRecordSummary, FuelRecordQueryParams>({
      fetchFn: fuelRecordApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<FuelRecord>();

  useEffect(() => {
    vehicleApi
      .list({ status: 1, pageSize: 100 })
      .then((res) => setVehicleOptions(res.data.items.map((v) => ({ label: v.plateNo, value: v.id }))))
      .catch(() => {});
  }, []);

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await fuelRecordApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {
      /* 已提示 */
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await fuelRecordApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {
      /* 已提示 */
    }
  };

  const columns: TableProps<FuelRecordSummary>['columns'] = [
    { title: '打油日期', dataIndex: 'fuelDate', key: 'fuelDate', width: 120 },
    { title: '车辆', dataIndex: 'vehiclePlateNo', key: 'vehiclePlateNo', width: 140, render: (v) => v || '-' },
    { title: '司机', dataIndex: 'driverName', key: 'driverName', width: 140, render: (v) => v || '-' },
    { title: '升数(L)', dataIndex: 'liters', key: 'liters', width: 100, render: (v) => v ?? '-' },
    { title: '金额(元)', dataIndex: 'amount', key: 'amount', width: 110, render: (v) => v ?? '-' },
    {
      title: '凭证',
      dataIndex: 'imageCount',
      key: 'imageCount',
      width: 90,
      render: (n: number) => (n > 0 ? <Tag color="blue">{n} 张</Tag> : '-'),
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (val) => formatDateTime(val) },
    {
      title: '操作',
      key: 'action',
      width: 140,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleEdit(record.id)}>编辑</a>
          <ConfirmAction
            title="确定要删除该打油记录吗？"
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
    <PageContainer title="打油记录">
      <FuelRecordSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} vehicleOptions={vehicleOptions} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建打油记录
          </Button>
        }
      >
        <Table
          columns={columns}
          dataSource={dataSource}
          rowKey="id"
          loading={loading}
          scroll={{ x: 1000 }}
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

      <FuelRecordFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        vehicleOptions={vehicleOptions}
        driverOptions={driverOptions}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default FuelRecordListPage;
