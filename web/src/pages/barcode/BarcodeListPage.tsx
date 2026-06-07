/* 条码列表页面 */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { barcodeApi } from '@/api/barcode';
import { packagingLevelApi } from '@/api/packagingLevel';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { BarcodeSummary, BarcodeQueryParams, Barcode } from '@/types/barcode';
import BarcodeSearchForm from './components/BarcodeSearchForm';
import BarcodeFormModal from './components/BarcodeFormModal';
import { formatDateTime } from '@/utils/format';

export interface LevelOption {
  label: string;
  value: number;
}

const BarcodeListPage: React.FC = () => {
  const [searchForm] = Form.useForm<BarcodeQueryParams>();
  const [levelOptions, setLevelOptions] = useState<LevelOption[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<BarcodeSummary, BarcodeQueryParams>({
      fetchFn: barcodeApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<Barcode>();

  useEffect(() => {
    packagingLevelApi
      .list({ page: 1, pageSize: 100, status: 1 })
      .then((res) => setLevelOptions(res.data.items.map((l) => ({ label: `${l.levelName}（${l.skuName ?? ''}）`, value: l.id }))))
      .catch(() => {});
  }, []);

  const handleEdit = async (id: number) => {
    try {
      const res = await barcodeApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };
  const handleDelete = async (id: number) => {
    try {
      await barcodeApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };
  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await barcodeApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<BarcodeSummary>['columns'] = [
    { title: '条码', dataIndex: 'barcode', key: 'barcode', width: 180 },
    { title: '类型', dataIndex: 'barcodeType', key: 'barcodeType', width: 100, render: (v) => <Tag>{v}</Tag> },
    { title: '所属包装层', dataIndex: 'levelName', key: 'levelName', width: 140, render: (v) => v || '-' },
    { title: '主条码', dataIndex: 'isPrimary', key: 'isPrimary', width: 90, render: (v) => (v ? <Tag color="blue">主</Tag> : '-') },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (val, record) => (
        <Switch checked={val === STATUS.ENABLED} onChange={(c) => handleStatusChange(record.id, c)} checkedChildren="有效" unCheckedChildren="失效" />
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
          <ConfirmAction title={`确定删除条码 [${record.barcode}] 吗？`} onConfirm={() => handleDelete(record.id)} type="link" danger style={{ padding: 0, height: 'auto' }}>删除</ConfirmAction>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer title="条码管理">
      <BarcodeSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} levelOptions={levelOptions} />
      <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => formModal.open('create')}>新建条码</Button>}>
        <Table columns={columns} dataSource={dataSource} rowKey="id" loading={loading} scroll={{ x: 980 }}
          pagination={{ current: page, pageSize, total, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条记录`, onChange: onPageChange }} />
      </Card>
      <BarcodeFormModal visible={formModal.visible} mode={formModal.mode} data={formModal.data} levelOptions={levelOptions} onClose={formModal.close} onSuccess={refresh} />
    </PageContainer>
  );
};

export default BarcodeListPage;
