/* 物料图片列表页面 */
import React, { useEffect, useState } from 'react';
import { Card, Table, Button, Space, Form, Switch, Tag, Image, message } from 'antd';
import type { TableProps } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import PageContainer from '@/components/PageContainer';
import ConfirmAction from '@/components/ConfirmAction';
import { itemImageApi } from '@/api/itemImage';
import { skuApi } from '@/api/sku';
import { useTable } from '@/hooks/useTable';
import { useModal } from '@/hooks/useModal';
import { STATUS } from '@/constants/enums';
import type { ItemImageSummary, ItemImageQueryParams, ItemImage } from '@/types/itemImage';
import ItemImageSearchForm from './components/ItemImageSearchForm';
import ItemImageFormModal from './components/ItemImageFormModal';
import { formatDateTime } from '@/utils/format';

export interface SkuOption {
  label: string;
  value: number;
}

const ItemImageListPage: React.FC = () => {
  const [searchForm] = Form.useForm<ItemImageQueryParams>();
  const [skuOptions, setSkuOptions] = useState<SkuOption[]>([]);

  const { dataSource, loading, total, page, pageSize, onPageChange, onSearch, onReset, refresh } =
    useTable<ItemImageSummary, ItemImageQueryParams>({
      fetchFn: itemImageApi.list,
      defaultParams: { page: 1, pageSize: 20 },
    });

  const formModal = useModal<ItemImage>();

  useEffect(() => {
    skuApi
      .list({ page: 1, pageSize: 100, status: 1 })
      .then((res) => setSkuOptions(res.data.items.map((s) => ({ label: `${s.skuName}（${s.skuCode}）`, value: s.id }))))
      .catch(() => {});
  }, []);

  const handleEdit = async (id: number) => {
    try {
      const res = await itemImageApi.getById(id);
      formModal.open('edit', res.data);
    } catch (e) {}
  };
  const handleDelete = async (id: number) => {
    try {
      await itemImageApi.delete(id);
      message.success('删除成功');
      refresh();
    } catch (e) {}
  };
  const handleStatusChange = async (id: number, checked: boolean) => {
    try {
      await itemImageApi.updateStatus(id, { status: checked ? STATUS.ENABLED : STATUS.DISABLED });
      message.success('状态变更成功');
      refresh();
    } catch (e) {}
  };

  const columns: TableProps<ItemImageSummary>['columns'] = [
    {
      title: '图片',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      width: 100,
      render: (url: string) => <Image src={url} width={48} height={48} style={{ objectFit: 'cover' }} fallback="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='48' height='48'%3E%3Crect width='48' height='48' fill='%23eee'/%3E%3C/svg%3E" />,
    },
    { title: '归属', key: 'owner', width: 160, render: (_, r) => (r.skuId ? `SKU#${r.skuId}` : r.spuId ? `SPU#${r.spuId}` : r.levelId ? `层#${r.levelId}` : '-') },
    { title: '类型', dataIndex: 'imageType', key: 'imageType', width: 120, render: (v) => v || '-' },
    { title: '排序', dataIndex: 'sortOrder', key: 'sortOrder', width: 80 },
    { title: '主图', dataIndex: 'isPrimary', key: 'isPrimary', width: 90, render: (v) => (v ? <Tag color="blue">主图</Tag> : '-') },
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
          <ConfirmAction title="确定删除该图片吗？" onConfirm={() => handleDelete(record.id)} type="link" danger style={{ padding: 0, height: 'auto' }}>删除</ConfirmAction>
        </Space>
      ),
    },
  ];

  return (
    <PageContainer title="物料图片">
      <ItemImageSearchForm form={searchForm} onSearch={onSearch} onReset={onReset} skuOptions={skuOptions} />
      <Card extra={<Button type="primary" icon={<PlusOutlined />} onClick={() => formModal.open('create')}>新建图片</Button>}>
        <Table columns={columns} dataSource={dataSource} rowKey="id" loading={loading} scroll={{ x: 1020 }}
          pagination={{ current: page, pageSize, total, showSizeChanger: true, showQuickJumper: true, showTotal: (t) => `共 ${t} 条记录`, onChange: onPageChange }} />
      </Card>
      <ItemImageFormModal visible={formModal.visible} mode={formModal.mode} data={formModal.data} skuOptions={skuOptions} onClose={formModal.close} onSuccess={refresh} />
    </PageContainer>
  );
};

export default ItemImageListPage;
