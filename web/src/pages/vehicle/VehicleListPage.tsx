/* ========================================
   车辆列表页面（运输管理）
   ======================================== */
import React from 'react';
import { Card, Table, Button, Form, Switch, Space, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { vehicleApi } from '@/api/vehicle';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { useCrewOptions } from '@/hooks/useCrewOptions';
import { STATUS } from '@/constants/enums';
import type { VehicleSummary, VehicleQueryParams, Vehicle } from '@/types/vehicle';
import VehicleSearchForm from './components/VehicleSearchForm';
import VehicleFormModal from './components/VehicleFormModal';
import { formatDateTime } from '@/utils/format';

const VehicleListPage: React.FC = () => {
  const [searchForm] = Form.useForm<VehicleQueryParams>();
  const { driverOptions, escortOptions } = useCrewOptions();

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<VehicleSummary, VehicleQueryParams>({
      fetchFn: vehicleApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<Vehicle>();

  const handleCreate = () => formModal.open('create');

  const handleEdit = async (id: number) => {
    try {
      const res = await vehicleApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {
      /* 全局拦截器已提示 */
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await vehicleApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {
      /* 全局拦截器已提示 */
    }
  };

  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await vehicleApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {
      /* 全局拦截器已提示 */
    }
  };

  const columns: TableProps<VehicleSummary>['columns'] = [
    { title: '车牌号', dataIndex: 'plateNo', key: 'plateNo', width: 140 },
    { title: '常态化司机', dataIndex: 'defaultDriverName', key: 'defaultDriverName', width: 180, render: (v) => v || '-' },
    { title: '常态化跟车员', dataIndex: 'defaultEscortName', key: 'defaultEscortName', width: 180, render: (v) => v || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch
          checked={val === STATUS.ENABLED}
          onChange={(checked) => handleStatusChange(record.id, checked)}
          checkedChildren="启用"
          unCheckedChildren="禁用"
        />
      ),
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
            title={`确定要删除车辆 [${record.plateNo}] 吗？`}
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
    <PageContainer title="车辆管理">
      <VehicleSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} />

      <Card
        extra={
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            新建车辆
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

      <VehicleFormModal
        visible={formModal.visible}
        mode={formModal.mode}
        data={formModal.data}
        driverOptions={driverOptions}
        escortOptions={escortOptions}
        onClose={formModal.close}
        onSuccess={refresh}
      />
    </PageContainer>
  );
};

export default VehicleListPage;
