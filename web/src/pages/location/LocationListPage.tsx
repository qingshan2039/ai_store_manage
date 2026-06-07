/* 库位列表页面 */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { locationApi } from '@/api/location';
import { warehouseApi } from '@/api/warehouse';
import { zoneApi } from '@/api/zone';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { LocationSummary, LocationQueryParams, Location } from '@/types/location';
import LocationSearchForm from './components/LocationSearchForm';
import LocationFormModal from './components/LocationFormModal';
import { formatDateTime } from '@/utils/format';

export interface Option {
  label: string;
  value: number;
}

const LocationListPage: React.FC = () => {
  const [searchForm] = Form.useForm<LocationQueryParams>();
  const [warehouseOptions, setWarehouseOptions] = useState<Option[]>([]);
  const [zoneOptions, setZoneOptions] = useState<Option[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<LocationSummary, LocationQueryParams>({ fetchFn: locationApi.list, defaultParams: { page: 1, pageSize: 20 } });

  const formModal = useModal<Location>();

  useEffect(() => {
    warehouseApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setWarehouseOptions(res.data.items.map((w) => ({ label: w.name, value: w.id })))).catch(() => {});
    zoneApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setZoneOptions(res.data.items.map((z) => ({ label: `${z.warehouseName ?? ''}/${z.name}`, value: z.id })))).catch(() => {});
  }, []);

  const handleEdit = async (id: number) => { try { const res = await locationApi.getById(id); formModal.open('edit', res.data); } catch (e) {} };
  const handleDelete = async (id: number) => { try { await locationApi.delete(id); message.success('删除成功'); refresh(); } catch (e) {} };
  const handleStatusChange = async (id: number, checked: boolean) => { try { await locationApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED }); message.success('状态变更成功'); refresh(); } catch (e) {} };

  const columns: TableProps<LocationSummary>['columns'] = [
    { title: '所属仓库', dataIndex: 'warehouseName', key: 'warehouseName', width: 160, render: (v) => v || '-' },
    { title: '所属库区', dataIndex: 'zoneName', key: 'zoneName', width: 140, render: (v) => v || '-' },
    { title: '库位编码', dataIndex: 'code', key: 'code', width: 140 },
    { title: '类型', dataIndex: 'locType', key: 'locType', width: 120, render: (v) => v || '-' },
    { title: '状态', dataIndex: 'status', key: 'status', width: 100, render: (val, r) => <Switch checked={val === STATUS.ENABLED} onChange={(c) => handleStatusChange(r.id, c)} checkedChildren="启用" unCheckedChildren="禁用" /> },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (v) => formatDateTime(v) },
    { title: '操作', key: 'action', width: 120, fixed: 'right', render: (_, r) => (
      <Space size="middle"><a onClick={() => handleEdit(r.id)}>编辑</a>
        <ConfirmAction title={`确定删除库位 [${r.code}] 吗？`} onConfirm={() => handleDelete(r.id)} type="link" danger style={{ padding: 0, height: 'auto' }}>删除</ConfirmAction>
      </Space>) },
  ];

  return (
    <PageContainer title="库位管理">
      <LocationSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} warehouseOptions={warehouseOptions} />
      <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => formModal.open('create')}>新建库位</Button>}>
        <Table columns={columns} dataSource={dataSource} rowKey="id" loading={loading} scroll={{ x: 1000 }}
          pagination={{ current: page, pageSize, total, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条记录`, onChange: onPageChange }} />
      </Card>
      <LocationFormModal visible={formModal.visible} mode={formModal.mode} data={formModal.data} warehouseOptions={warehouseOptions} zoneOptions={zoneOptions} onClose={formModal.close} onSuccess={refresh} />
    </PageContainer>
  );
};

export default LocationListPage;
