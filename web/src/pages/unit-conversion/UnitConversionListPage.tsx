/* 计量换算列表页面 */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { unitConversionApi } from '@/api/unitConversion';
import { skuApi } from '@/api/sku';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { UnitConversionSummary, UnitConversionQueryParams, UnitConversion } from '@/types/unitConversion';
import UnitConversionSearchForm from './components/UnitConversionSearchForm';
import UnitConversionFormModal from './components/UnitConversionFormModal';
import { formatDateTime } from '@/utils/format';

export interface SkuOption {
  label: string;
  value: number;
}

const UnitConversionListPage: React.FC = () => {
  const [searchForm] = Form.useForm<UnitConversionQueryParams>();
  const [skuOptions, setSkuOptions] = useState<SkuOption[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<UnitConversionSummary, UnitConversionQueryParams>({
      fetchFn: unitConversionApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<UnitConversion>();

  useEffect(() => {
    skuApi
      .list({ page: 1, pageSize: 100, status: 1 })
      .then((res) => setSkuOptions(res.data.items.map((s) => ({ label: `${s.skuName}（${s.skuCode}）`, value: s.id }))))
      .catch(() => {});
  }, []);

  const handleEdit = async (id: number) => {
    try {
      const res = await unitConversionApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };
  const handleDelete = async (id: number) => {
    try {
      await unitConversionApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };
  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await unitConversionApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<UnitConversionSummary>['columns'] = [
    { title: '所属 SKU', dataIndex: 'skuName', key: 'skuName', width: 220, ellipsis: true, render: (v) => v || '-' },
    { title: '换算', key: 'conv', width: 200, render: (_, r) => `${r.fromUnit} → ${r.toUnit}` },
    { title: '系数', dataIndex: 'factor', key: 'factor', width: 140 },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch checked={val === STATUS.ENABLED} onChange={(c) => handleStatusChange(record.id, c)} checkedChildren="启用" unCheckedChildren="禁用" />
      ),
    },
    { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt', width: 160, render: (v) => formatDateTime(v) },
    {
      title: '操作',
      key: 'action',
      width: 120,
      fixed: 'right',
      render: (_, record) => (
        <Space size="middle">
          <a onClick={() => handleEdit(record.id)}>编辑</a>
          <ConfirmAction title="确定删除该换算吗？" onConfirm={() => handleDelete(record.id)} type="link" danger style={{ padding: 0, height: 'auto' }}>删除</ConfirmAction>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer title="计量换算">
      <UnitConversionSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} skuOptions={skuOptions} />
      <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => formModal.open('create')}>新建换算</Button>}>
        <Table columns={columns} dataSource={dataSource} rowKey="id" loading={loading} scroll={{ x: 940 }}
          pagination={{ current: page, pageSize, total, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条记录`, onChange: onPageChange }} />
      </Card>
      <UnitConversionFormModal visible={formModal.visible} mode={formModal.mode} data={formModal.data} skuOptions={skuOptions} onClose={formModal.close} onSuccess={refresh} />
    </PageContainer>
  );
};

export default UnitConversionListPage;
