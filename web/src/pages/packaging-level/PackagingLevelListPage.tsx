/* 包装层级列表页面 */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, Tag, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { packagingLevelApi } from '@/api/packagingLevel';
import { skuApi } from '@/api/sku';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { PackagingLevelSummary, PackagingLevelQueryParams, PackagingLevel } from '@/types/packagingLevel';
import PackagingLevelSearchForm from './components/PackagingLevelSearchForm';
import PackagingLevelFormModal from './components/PackagingLevelFormModal';
import { formatDateTime } from '@/utils/format';

export interface SkuOption {
  label: string;
  value: number;
}

const PackagingLevelListPage: React.FC = () => {
  const [searchForm] = Form.useForm<PackagingLevelQueryParams>();
  const [skuOptions, setSkuOptions] = useState<SkuOption[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<PackagingLevelSummary, PackagingLevelQueryParams>({
      fetchFn: packagingLevelApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<PackagingLevel>();

  useEffect(() => {
    skuApi
      .list({ page: 1, pageSize: 100, status: 1 })
      .then((res) => setSkuOptions(res.data.items.map((s) => ({ label: `${s.skuName}（${s.skuCode}）`, value: s.id }))))
      .catch(() => {});
  }, []);

  const handleEdit = async (id: number) => {
    try {
      const res = await packagingLevelApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };
  const handleDelete = async (id: number) => {
    try {
      await packagingLevelApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };
  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await packagingLevelApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<PackagingLevelSummary>['columns'] = [
    { title: '所属 SKU', dataIndex: 'skuName', key: 'skuName', width: 200, ellipsis: true, render: (v) => v || '-' },
    { title: '层级', dataIndex: 'levelName', key: 'levelName', width: 100 },
    { title: '序号', dataIndex: 'levelSeq', key: 'levelSeq', width: 80 },
    { title: '单位', dataIndex: 'unitCode', key: 'unitCode', width: 100 },
    { title: '基本单位', dataIndex: 'isBaseUnit', key: 'isBaseUnit', width: 100, render: (v) => (v ? <Tag color="blue">基本</Tag> : '-') },
    { title: '可售', dataIndex: 'isSellable', key: 'isSellable', width: 80, render: (v) => (v ? <Tag color="green">可售</Tag> : '-') },
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
          <ConfirmAction title={`确定删除层级 [${record.levelName}] 吗？`} onConfirm={() => handleDelete(record.id)} type="link" danger style={{ padding: 0, height: 'auto' }}>
            删除
          </ConfirmAction>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer title="包装层级">
      <PackagingLevelSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} skuOptions={skuOptions} />
      <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => formModal.open('create')}>新建层级</Button>}>
        <Table columns={columns} dataSource={dataSource} rowKey="id" loading={loading} scroll={{ x: 1080 }}
          pagination={{ current: page, pageSize, total, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条记录`, onChange: onPageChange }} />
      </Card>
      <PackagingLevelFormModal visible={formModal.visible} mode={formModal.mode} data={formModal.data} skuOptions={skuOptions} onClose={formModal.close} onSuccess={refresh} />
    </PageContainer>
  );
};

export default PackagingLevelListPage;
