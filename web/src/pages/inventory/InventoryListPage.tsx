/* 库存查询列表页面 */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { inventoryApi } from '@/api/inventory';
import { skuApi } from '@/api/sku';
import { lpnApi } from '@/api/lpn';
import { locationApi } from '@/api/location';
import { zoneApi } from '@/api/zone';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import type { InventorySummaryItem, InventoryQueryParams, Inventory } from '@/types/inventory';
import InventorySearchForm from './components/InventorySearchForm';
import InventoryFormModal from './components/InventoryFormModal';
import { formatDateTime } from '@/utils/format';

export interface Option {
  label: string;
  value: number;
}

const InventoryListPage: React.FC = () => {
  const [searchForm] = Form.useForm<InventoryQueryParams>();
  const [skuOptions, setSkuOptions] = useState<Option[]>([]);
  const [lpnOptions, setLpnOptions] = useState<Option[]>([]);
  const [locationOptions, setLocationOptions] = useState<Option[]>([]);
  const [zoneOptions, setZoneOptions] = useState<Option[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<InventorySummaryItem, InventoryQueryParams>({ fetchFn: inventoryApi.list, defaultParams: { page: 1, pageSize: 20 } });

  const formModal = useModal<Inventory>();

  useEffect(() => {
    skuApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setSkuOptions(res.data.items.map((s) => ({ label: `${s.skuName}（${s.skuCode}）`, value: s.id })))).catch(() => {});
    lpnApi.list({ page: 1, pageSize: 100 }).then((res) => setLpnOptions(res.data.items.map((l) => ({ label: l.lpnCode, value: l.id })))).catch(() => {});
    locationApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setLocationOptions(res.data.items.map((l) => ({ label: `${l.code}（${l.warehouseName ?? ''}）`, value: l.id })))).catch(() => {});
    zoneApi.list({ page: 1, pageSize: 100, status: 1 }).then((res) => setZoneOptions(res.data.items.map((z) => ({ label: `${z.warehouseName ?? ''}/${z.name}`, value: z.id })))).catch(() => {});
  }, []);

  const handleEdit = async (id: number) => { try { const res = await inventoryApi.getById(id); formModal.open('edit', res.data); } catch (e) {} };
  const handleDelete = async (id: number) => { try { await inventoryApi.delete(id); message.success('删除成功'); refresh(); } catch (e) {} };

  const columns: TableProps<InventorySummaryItem>['columns'] = [
    { title: '所属 SKU', dataIndex: 'skuName', key: 'skuName', width: 220, ellipsis: true, render: (v) => v || '-' },
    { title: '托盘名称', dataIndex: 'lpnCode', key: 'lpnCode', width: 140, render: (v) => v || '-' },
    { title: '托盘类型', dataIndex: 'palletTypeName', key: 'palletTypeName', width: 160, render: (v) => v || '-' },
    { title: '库位', dataIndex: 'locationCode', key: 'locationCode', width: 120, render: (v) => v || '-' },
    { title: '批次', dataIndex: 'lotNo', key: 'lotNo', width: 120, render: (v) => v || '-' },
    { title: '在库', dataIndex: 'qtyOnHand', key: 'qtyOnHand', width: 100 },
    { title: '锁定', dataIndex: 'qtyReserved', key: 'qtyReserved', width: 100 },
    { title: '可用', dataIndex: 'qtyAvailable', key: 'qtyAvailable', width: 100 },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (v) => formatDateTime(v) },
    { title: '操作', key: 'action', width: 120, fixed: 'right', render: (_, r) => (
      <Space size="middle"><a onClick={() => handleEdit(r.id)}>编辑</a>
        <ConfirmAction title="确定删除该库存记录吗？" onConfirm={() => handleDelete(r.id)} type="link" danger style={{ padding: 0, height: 'auto' }}>删除</ConfirmAction>
      </Space>) },
  ];

  return (
    <PageContainer title="库存查询">
      <InventorySearchForm form={searchForm} onSearch={onSearch} onReset={onReset} skuOptions={skuOptions} />
      <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => formModal.open('create')}>新增库存</Button>}>
        <Table columns={columns} dataSource={dataSource} rowKey="id" loading={loading} scroll={{ x: 1320 }}
          pagination={{ current: page, pageSize, total, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条记录`, onChange: onPageChange }} />
      </Card>
      <InventoryFormModal visible={formModal.visible} mode={formModal.mode} data={formModal.data} skuOptions={skuOptions} lpnOptions={lpnOptions} locationOptions={locationOptions} zoneOptions={zoneOptions} onClose={formModal.close} onSuccess={refresh} />
    </PageContainer>
  );
};

export default InventoryListPage;
