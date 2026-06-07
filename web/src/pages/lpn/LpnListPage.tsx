/* 托盘实例列表页面 */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Select, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { lpnApi } from '@/api/lpn';
import { palletTypeApi } from '@/api/pallet';
import { warehouseApi } from '@/api/warehouse';
import { locationApi } from '@/api/location';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { LPN_STATUS_MAP, LPN_STATUS_OPTIONS } from '@/constants/enums';
import type { LpnSummary, LpnQueryParams, Lpn, LpnStatus } from '@/types/lpn';
import LpnSearchForm from './components/LpnSearchForm';
import LpnFormModal from './components/LpnFormModal';
import { formatDateTime } from '@/utils/format';

export interface Option {
  label: string;
  value: number;
}

const STATUS_COLOR: Record<string, string> = { IN_STOCK: 'green', IN_TRANSIT: 'orange', EMPTY: 'default' };

const LpnListPage: React.FC = () => {
  const [searchForm] = Form.useForm<LpnQueryParams>();
  const [palletTypeOptions, setPalletTypeOptions] = useState<Option[]>([]);
  const [warehouseOptions, setWarehouseOptions] = useState<Option[]>([]);
  const [locationOptions, setLocationOptions] = useState<Option[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<LpnSummary, LpnQueryParams>({ fetchFn: lpnApi.list, defaultParams: { page: 1, pageSize: 20 } });

  const formModal = useModal<Lpn>();

  useEffect(() => {
    palletTypeApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setPalletTypeOptions(res.data.items.map((p) => ({ label: p.name, value: p.id })))).catch(() => {});
    warehouseApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setWarehouseOptions(res.data.items.map((w) => ({ label: w.name, value: w.id })))).catch(() => {});
    locationApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setLocationOptions(res.data.items.map((l) => ({ label: `${l.code}（${l.warehouseName ?? ''}）`, value: l.id })))).catch(() => {});
  }, []);

  const handleEdit = async (id: number) => { try { const res = await lpnApi.getById(id); formModal.open('edit', res.data); } catch (e) {} };
  const handleDelete = async (id: number) => { try { await lpnApi.delete(id); message.success('删除成功'); refresh(); } catch (e) {} };
  const handleStatusChange = async (id: number, status: LpnStatus) => { try { await lpnApi.updateStatus(id, { status }); message.success('状态变更成功'); refresh(); } catch (e) {} };

  const columns: TableProps<LpnSummary>['columns'] = [
    { title: '托盘号', dataIndex: 'lpnCode', key: 'lpnCode', width: 160 },
    { title: '托盘类型', dataIndex: 'palletTypeName', key: 'palletTypeName', width: 160, render: (v) => v || '-' },
    { title: '所属仓库', dataIndex: 'warehouseName', key: 'warehouseName', width: 140, render: (v) => v || '-' },
    { title: '库位', dataIndex: 'locationCode', key: 'locationCode', width: 120, render: (v) => v || '-' },
    { title: '状态', dataIndex: 'status', key: 'status', width: 130, render: (val: LpnStatus, r) => (
      <Select size="small" value={val} options={LPN_STATUS_OPTIONS} style={{ width: 100 }} onChange={(s) => handleStatusChange(r.id, s as LpnStatus)}
        optionRender={(o) => <Tag color={STATUS_COLOR[o.value as string]} style={{ margin: 0 }}>{LPN_STATUS_MAP[o.value as string]}</Tag>} /> ) },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (v) => formatDateTime(v) },
    { title: '操作', key: 'action', width: 120, fixed: 'right', render: (_, r) => (
      <Space size="middle"><a onClick={() => handleEdit(r.id)}>编辑</a>
        <ConfirmAction title={`确定删除托盘 [${r.lpnCode}] 吗？`} onConfirm={() => handleDelete(r.id)} type="link" danger style={{ padding: 0, height: 'auto' }}>删除</ConfirmAction>
      </Space>) },
  ];

  return (
    <PageContainer title="托盘实例">
      <LpnSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} warehouseOptions={warehouseOptions} />
      <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => formModal.open('create')}>新建托盘</Button>}>
        <Table columns={columns} dataSource={dataSource} rowKey="id" loading={loading} scroll={{ x: 1010 }}
          pagination={{ current: page, pageSize, total, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条记录`, onChange: onPageChange }} />
      </Card>
      <LpnFormModal visible={formModal.visible} mode={formModal.mode} data={formModal.data} palletTypeOptions={palletTypeOptions} warehouseOptions={warehouseOptions} locationOptions={locationOptions} onClose={formModal.close} onSuccess={refresh} />
    </PageContainer>
  );
};

export default LpnListPage;
